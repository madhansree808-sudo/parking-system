"""
=============================================================================
SMART PARKING USAGE ANALYSIS & VISUALIZATION SYSTEM
Course: 25ITC31 - Foundations of Data Science
Student: Madhan sree (Roll: 732925ITR054, Reg No: 54, Class: 25IT3A)
Institution: Velalar College of Engineering and Technology, Erode - 12
=============================================================================
This script retrieves transactional parking records from the SQLite database,
conducts data wrangling & preprocessing (Pandas/NumPy), and generates
meaningful exploratory data visualizations (Matplotlib/Seaborn).
"""

import os
import sqlite3
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns

# Set publication-quality visualization styling
sns.set_theme(style="whitegrid", palette="deep")
plt.rcParams['font.sans-serif'] = 'DejaVu Sans'
plt.rcParams['figure.dpi'] = 300

DB_PATH = os.path.join("database", "parking_system.db")
OUTPUT_DIR = os.path.join("output", "visualizations")
SCHEMA_PATH = os.path.join("database", "schema.sql")
SEED_PATH = os.path.join("database", "seed_data.sql")


def ensure_database_and_dirs():
    """Ensures output directories and SQLite database exist."""
    os.makedirs(OUTPUT_DIR, exist_ok=True)
    os.makedirs("database", exist_ok=True)

    if not os.path.exists(DB_PATH) or os.path.getsize(DB_PATH) == 0:
        print("[INFO] Database file not found or empty. Initializing from SQL scripts...")
        conn = sqlite3.connect(DB_PATH)
        cursor = conn.cursor()
        
        if os.path.exists(SCHEMA_PATH):
            with open(SCHEMA_PATH, 'r', encoding='utf-8') as f:
                cursor.executescript(f.read())
        if os.path.exists(SEED_PATH):
            with open(SEED_PATH, 'r', encoding='utf-8') as f:
                cursor.executescript(f.read())
                
        conn.commit()
        conn.close()
        print("[SUCCESS] Database initialized and seeded successfully.")


def load_dataset_from_db():
    """Extracts relational tables into Pandas DataFrames."""
    conn = sqlite3.connect(DB_PATH)

    query_areas = "SELECT * FROM parking_areas;"
    query_slots = "SELECT * FROM parking_slots;"
    query_vehicles = "SELECT * FROM vehicles;"
    query_tickets = """
        SELECT 
            t.ticket_id,
            t.vehicle_number,
            v.vehicle_type,
            v.owner_name,
            t.slot_id,
            s.area_id,
            a.area_name,
            a.location,
            t.entry_time,
            t.exit_time,
            t.parking_duration_hours,
            t.fee_amount,
            t.payment_status
        FROM parking_tickets t
        LEFT JOIN vehicles v ON t.vehicle_number = v.vehicle_number
        LEFT JOIN parking_slots s ON t.slot_id = s.slot_id
        LEFT JOIN parking_areas a ON s.area_id = a.area_id;
    """

    df_areas = pd.read_sql_query(query_areas, conn)
    df_slots = pd.read_sql_query(query_slots, conn)
    df_vehicles = pd.read_sql_query(query_vehicles, conn)
    df_tickets = pd.read_sql_query(query_tickets, conn)

    conn.close()
    return df_areas, df_slots, df_vehicles, df_tickets


def preprocess_data(df_tickets):
    """
    Cleans and prepares data:
    - Parses timestamps to datetime objects
    - Imputes and handles missing values
    - Engineers temporal features (hour, day_of_week, duration bins)
    """
    df = df_tickets.copy()

    # Convert timestamps
    df['entry_time'] = pd.to_datetime(df['entry_time'])
    df['exit_time'] = pd.to_datetime(df['exit_time'])

    # Feature Engineering
    df['entry_hour'] = df['entry_time'].dt.hour
    df['day_of_week'] = df['entry_time'].dt.day_name()
    df['date'] = df['entry_time'].dt.date

    # Distinguish completed vs active sessions
    df['is_active'] = df['exit_time'].isnull()

    # Categorize duration intervals for completed tickets
    completed = df[~df['is_active']].copy()
    bins = [0, 2, 4, 6, 24]
    labels = ['Short Stay (<=2 hrs)', 'Medium Stay (2-4 hrs)', 'Extended Stay (4-6 hrs)', 'Long Stay (>6 hrs)']
    completed['duration_category'] = pd.cut(completed['parking_duration_hours'], bins=bins, labels=labels)

    return df, completed


def plot_occupancy_by_area(df_areas, df_slots):
    """Visualization 1: Area-wise Slot Capacity vs Current Occupancy (Bar Chart)"""
    # Calculate occupancy per area
    occupancy = df_slots.groupby(['area_id', 'status']).size().unstack(fill_value=0)
    
    # Merge with area metadata
    merged = df_areas.merge(occupancy, on='area_id', how='left').fillna(0)
    if 'OCCUPIED' not in merged.columns:
        merged['OCCUPIED'] = 0
    if 'AVAILABLE' not in merged.columns:
        merged['AVAILABLE'] = 0

    x = np.arange(len(merged))
    width = 0.35

    fig, ax = plt.subplots(figsize=(10, 6))
    bars1 = ax.bar(x - width/2, merged['total_slot_capacity'], width, label='Total Capacity', color='#3498db', edgecolor='black')
    bars2 = ax.bar(x + width/2, merged['OCCUPIED'], width, label='Currently Occupied', color='#e74c3c', edgecolor='black')

    ax.set_title('Parking Area Capacity vs. Real-Time Occupancy', fontsize=14, fontweight='bold', pad=15)
    ax.set_xlabel('Parking Area Name', fontsize=12, fontweight='bold')
    ax.set_ylabel('Number of Slots', fontsize=12, fontweight='bold')
    ax.set_xticks(x)
    ax.set_xticklabels(merged['area_name'], rotation=15, ha='right', fontsize=10)
    ax.legend(frameon=True, facecolor='white', framealpha=1)
    ax.grid(axis='y', linestyle='--', alpha=0.7)

    # Add data value labels
    for bar in bars1:
        yval = bar.get_height()
        ax.text(bar.get_x() + bar.get_width()/2.0, yval + 0.3, f"{int(yval)}", ha='center', va='bottom', fontsize=9, fontweight='bold')
    for bar in bars2:
        yval = bar.get_height()
        ax.text(bar.get_x() + bar.get_width()/2.0, yval + 0.3, f"{int(yval)}", ha='center', va='bottom', fontsize=9, fontweight='bold', color='#c0392b')

    plt.tight_layout()
    filepath = os.path.join(OUTPUT_DIR, "occupancy_by_area.png")
    plt.savefig(filepath)
    plt.close()
    print(f"[CHART GENERATED] {filepath}")


def plot_vehicle_category_distribution(df_tickets):
    """Visualization 2: Vehicle Classification Proportions (Donut Chart)"""
    counts = df_tickets['vehicle_type'].value_counts()
    colors = ['#2ecc71', '#3498db', '#f1c40f', '#9b59b6']

    fig, ax = plt.subplots(figsize=(8, 8))
    wedges, texts, autotexts = ax.pie(
        counts, 
        labels=counts.index, 
        autopct='%1.1f%%', 
        startangle=140, 
        colors=colors,
        pctdistance=0.78,
        wedgeprops=dict(width=0.45, edgecolor='black', linewidth=1.5)
    )

    for autotext in autotexts:
        autotext.set_color('black')
        autotext.set_fontweight('bold')
        autotext.set_fontsize(11)

    ax.set_title('Vehicle Ingress Distribution by Category', fontsize=14, fontweight='bold', pad=20)
    plt.tight_layout()
    filepath = os.path.join(OUTPUT_DIR, "vehicle_category_distribution.png")
    plt.savefig(filepath)
    plt.close()
    print(f"[CHART GENERATED] {filepath}")


def plot_peak_parking_hours(df_tickets):
    """Visualization 3: Diurnal Parking Ingress Traffic & Peak Hours (Line & Histogram with KDE)"""
    fig, ax = plt.subplots(figsize=(11, 6))

    sns.histplot(
        data=df_tickets, 
        x='entry_hour', 
        bins=range(7, 24), 
        kde=True, 
        color='#2980b9', 
        edgecolor='black', 
        alpha=0.6,
        ax=ax
    )

    ax.set_title('Diurnal Vehicle Ingress Patterns & Peak Parking Hours', fontsize=14, fontweight='bold', pad=15)
    ax.set_xlabel('Hour of Day (24-Hour Clock)', fontsize=12, fontweight='bold')
    ax.set_ylabel('Number of Vehicle Arrivals', fontsize=12, fontweight='bold')
    ax.set_xticks(range(7, 23))
    ax.set_xticklabels([f"{h:02d}:00" for h in range(7, 23)], rotation=45)
    ax.grid(True, linestyle='--', alpha=0.5)

    # Highlight peak morning and evening zones
    ax.axvspan(8.5, 10.5, color='orange', alpha=0.2, label='Morning Peak (09:00 - 10:00)')
    ax.axvspan(17.0, 19.0, color='red', alpha=0.15, label='Evening Peak (17:00 - 19:00)')
    ax.legend(frameon=True, facecolor='white', framealpha=1)

    plt.tight_layout()
    filepath = os.path.join(OUTPUT_DIR, "peak_parking_hours.png")
    plt.savefig(filepath)
    plt.close()
    print(f"[CHART GENERATED] {filepath}")


def plot_revenue_by_vehicle_type(df_completed):
    """Visualization 4: Cumulative Parking Revenue Collection by Vehicle Category (Bar Chart)"""
    revenue_summary = df_completed.groupby('vehicle_type')['fee_amount'].agg(['sum', 'count']).reset_index()
    revenue_summary.rename(columns={'sum': 'total_revenue', 'count': 'ticket_count'}, inplace=True)
    revenue_summary.sort_values(by='total_revenue', ascending=True, inplace=True)

    fig, ax = plt.subplots(figsize=(10, 6))
    bars = ax.barh(revenue_summary['vehicle_type'], revenue_summary['total_revenue'], color='#27ae60', edgecolor='black', height=0.55)

    ax.set_title('Cumulative Parking Revenue Collection by Vehicle Type', fontsize=14, fontweight='bold', pad=15)
    ax.set_xlabel('Total Revenue Collected (INR ₹)', fontsize=12, fontweight='bold')
    ax.set_ylabel('Vehicle Category', fontsize=12, fontweight='bold')
    ax.grid(axis='x', linestyle='--', alpha=0.7)

    for bar in bars:
        width = bar.get_width()
        ax.text(width + 15, bar.get_y() + bar.get_height()/2.0, f"₹{width:,.2f}", ha='left', va='center', fontsize=10, fontweight='bold', color='#145a32')

    plt.tight_layout()
    filepath = os.path.join(OUTPUT_DIR, "revenue_by_vehicle_type.png")
    plt.savefig(filepath)
    plt.close()
    print(f"[CHART GENERATED] {filepath}")


def plot_parking_duration_boxplot(df_completed):
    """Visualization 5: Parking Duration Spread & Quartile Analysis (Box Plot)"""
    fig, ax = plt.subplots(figsize=(10, 6))

    sns.boxplot(
        data=df_completed, 
        x='vehicle_type', 
        y='parking_duration_hours', 
        palette='Set2', 
        ax=ax,
        fliersize=5,
        linewidth=1.5
    )

    ax.set_title('Parking Duration Distribution by Vehicle Category', fontsize=14, fontweight='bold', pad=15)
    ax.set_xlabel('Vehicle Classification', fontsize=12, fontweight='bold')
    ax.set_ylabel('Parking Duration (Hours)', fontsize=12, fontweight='bold')
    ax.grid(axis='y', linestyle='--', alpha=0.7)

    plt.tight_layout()
    filepath = os.path.join(OUTPUT_DIR, "parking_duration_boxplot.png")
    plt.savefig(filepath)
    plt.close()
    print(f"[CHART GENERATED] {filepath}")


def print_descriptive_statistics(df_tickets, df_completed):
    """Prints tabular descriptive statistics to the console."""
    print("\n" + "="*80)
    print("      DATA SCIENCE STATISTICAL SUMMARY & USAGE ANALYTICS REPORT")
    print("="*80)
    print(f"Total Parking Transactions Recorded : {len(df_tickets)}")
    print(f"Completed / Settled Visits          : {len(df_completed)}")
    print(f"Currently Parked Vehicles (Active)  : {len(df_tickets[df_tickets['is_active']])}")
    print(f"Total Cumulative Revenue Collected  : Rs. {df_completed['fee_amount'].sum():,.2f}")
    print(f"Average Parking Duration            : {df_completed['parking_duration_hours'].mean():.2f} hours")
    print(f"Median Parking Duration             : {df_completed['parking_duration_hours'].median():.2f} hours")
    print(f"Maximum Parking Duration Recorded   : {df_completed['parking_duration_hours'].max():.2f} hours")
    print("-"*80)
    print("\nVehicle Category Breakdown (Completed Sessions):")
    cat_summary = df_completed.groupby('vehicle_type').agg(
        Total_Visits=('ticket_id', 'count'),
        Total_Revenue=('fee_amount', 'sum'),
        Avg_Fee=('fee_amount', 'mean'),
        Avg_Duration=('parking_duration_hours', 'mean')
    ).round(2)
    print(cat_summary.to_string())
    print("\n" + "="*80)


def main():
    print(">>> Starting Smart Parking Usage Analysis Sub-System...")
    ensure_database_and_dirs()
    df_areas, df_slots, df_vehicles, df_tickets = load_dataset_from_db()
    
    print(f"[INFO] Successfully loaded {len(df_tickets)} ticket records from database.")
    df_all, df_completed = preprocess_data(df_tickets)

    print("\n>>> Generating Exploratory Visualizations...")
    plot_occupancy_by_area(df_areas, df_slots)
    plot_vehicle_category_distribution(df_all)
    plot_peak_parking_hours(df_all)
    plot_revenue_by_vehicle_type(df_completed)
    plot_parking_duration_boxplot(df_completed)

    print_descriptive_statistics(df_all, df_completed)
    print("\n[SUCCESS] Python Analytics & Visualization execution completed!")
    print(f"All chart images saved to directory: {os.path.abspath(OUTPUT_DIR)}\n")


if __name__ == "__main__":
    main()
