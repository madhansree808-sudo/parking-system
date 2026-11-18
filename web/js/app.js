/**
 * =============================================================================
 * SMART PARKING MANAGEMENT & PARKING USAGE ANALYSIS WEB APPLICATION
 * Course Integration: 25ITT31, 25ITT32, 25ITT33, 25ITC31
 * Student: Madhan sree (Roll: 732925ITR054, Reg No: 54, Class: 25IT3A)
 * Institution: Velalar College of Engineering and Technology, Erode - 12
 * =============================================================================
 */

// Initial Seed Data aligned with database/seed_data.sql
const DEFAULT_AREAS = [
  { areaId: 'AREA-A', areaName: 'North Wing Ground', location: 'Gate 1 - Ground Level Block A', capacity: 12 },
  { areaId: 'AREA-B', areaName: 'South Deck Basement 1', location: 'Gate 2 - Basement Ramp B', capacity: 10 },
  { areaId: 'AREA-C', areaName: 'East Plaza Open Deck', location: 'Gate 3 - Surface Lot East', capacity: 10 },
  { areaId: 'AREA-D', areaName: 'VIP & EV Charging Hub', location: 'Terminal Hub Central Concourse', capacity: 8 }
];

const DEFAULT_SLOTS = [
  // Area A
  { slotId: 'A-TW-01', areaId: 'AREA-A', slotNumber: 'TW-101', type: 'TWO_WHEELER', status: 'OCCUPIED', vehicle: 'TN-33-AB-1001' },
  { slotId: 'A-TW-02', areaId: 'AREA-A', slotNumber: 'TW-102', type: 'TWO_WHEELER', status: 'OCCUPIED', vehicle: 'TN-33-BC-2002' },
  { slotId: 'A-TW-03', areaId: 'AREA-A', slotNumber: 'TW-103', type: 'TWO_WHEELER', status: 'AVAILABLE', vehicle: null },
  { slotId: 'A-TW-04', areaId: 'AREA-A', slotNumber: 'TW-104', type: 'TWO_WHEELER', status: 'AVAILABLE', vehicle: null },
  { slotId: 'A-FW-01', areaId: 'AREA-A', slotNumber: 'FW-101', type: 'FOUR_WHEELER', status: 'OCCUPIED', vehicle: 'TN-33-FG-6006' },
  { slotId: 'A-FW-02', areaId: 'AREA-A', slotNumber: 'FW-102', type: 'FOUR_WHEELER', status: 'OCCUPIED', vehicle: 'TN-33-GH-7007' },
  { slotId: 'A-FW-03', areaId: 'AREA-A', slotNumber: 'FW-103', type: 'FOUR_WHEELER', status: 'AVAILABLE', vehicle: null },
  { slotId: 'A-FW-04', areaId: 'AREA-A', slotNumber: 'FW-104', type: 'FOUR_WHEELER', status: 'AVAILABLE', vehicle: null },
  { slotId: 'A-FW-05', areaId: 'AREA-A', slotNumber: 'FW-105', type: 'FOUR_WHEELER', status: 'AVAILABLE', vehicle: null },
  { slotId: 'A-FW-06', areaId: 'AREA-A', slotNumber: 'FW-106', type: 'FOUR_WHEELER', status: 'AVAILABLE', vehicle: null },
  { slotId: 'A-HV-01', areaId: 'AREA-A', slotNumber: 'HV-101', type: 'HEAVY_VEHICLE', status: 'OCCUPIED', vehicle: 'TN-33-WX-2020' },
  { slotId: 'A-HV-02', areaId: 'AREA-A', slotNumber: 'HV-102', type: 'HEAVY_VEHICLE', status: 'AVAILABLE', vehicle: null },

  // Area B
  { slotId: 'B-FW-01', areaId: 'AREA-B', slotNumber: 'FW-201', type: 'FOUR_WHEELER', status: 'OCCUPIED', vehicle: 'TN-33-HJ-8008' },
  { slotId: 'B-FW-02', areaId: 'AREA-B', slotNumber: 'FW-202', type: 'FOUR_WHEELER', status: 'OCCUPIED', vehicle: 'TN-33-JK-9009' },
  { slotId: 'B-FW-03', areaId: 'AREA-B', slotNumber: 'FW-203', type: 'FOUR_WHEELER', status: 'AVAILABLE', vehicle: null },
  { slotId: 'B-FW-04', areaId: 'AREA-B', slotNumber: 'FW-204', type: 'FOUR_WHEELER', status: 'AVAILABLE', vehicle: null },
  { slotId: 'B-FW-05', areaId: 'AREA-B', slotNumber: 'FW-205', type: 'FOUR_WHEELER', status: 'AVAILABLE', vehicle: null },
  { slotId: 'B-TW-01', areaId: 'AREA-B', slotNumber: 'TW-201', type: 'TWO_WHEELER', status: 'AVAILABLE', vehicle: null },
  { slotId: 'B-TW-02', areaId: 'AREA-B', slotNumber: 'TW-202', type: 'TWO_WHEELER', status: 'AVAILABLE', vehicle: null },
  { slotId: 'B-TW-03', areaId: 'AREA-B', slotNumber: 'TW-203', type: 'TWO_WHEELER', status: 'AVAILABLE', vehicle: null },
  { slotId: 'B-TW-04', areaId: 'AREA-B', slotNumber: 'TW-204', type: 'TWO_WHEELER', status: 'AVAILABLE', vehicle: null },
  { slotId: 'B-TW-05', areaId: 'AREA-B', slotNumber: 'TW-205', type: 'TWO_WHEELER', status: 'MAINTENANCE', vehicle: null },

  // Area C
  { slotId: 'C-FW-01', areaId: 'AREA-C', slotNumber: 'FW-301', type: 'FOUR_WHEELER', status: 'AVAILABLE', vehicle: null },
  { slotId: 'C-FW-02', areaId: 'AREA-C', slotNumber: 'FW-302', type: 'FOUR_WHEELER', status: 'AVAILABLE', vehicle: null },
  { slotId: 'C-FW-03', areaId: 'AREA-C', slotNumber: 'FW-303', type: 'FOUR_WHEELER', status: 'OCCUPIED', vehicle: 'TN-40-DD-4141' },
  { slotId: 'C-FW-04', areaId: 'AREA-C', slotNumber: 'FW-304', type: 'FOUR_WHEELER', status: 'AVAILABLE', vehicle: null },
  { slotId: 'C-FW-05', areaId: 'AREA-C', slotNumber: 'FW-305', type: 'FOUR_WHEELER', status: 'AVAILABLE', vehicle: null },
  { slotId: 'C-HV-01', areaId: 'AREA-C', slotNumber: 'HV-301', type: 'HEAVY_VEHICLE', status: 'AVAILABLE', vehicle: null },
  { slotId: 'C-HV-02', areaId: 'AREA-C', slotNumber: 'HV-302', type: 'HEAVY_VEHICLE', status: 'AVAILABLE', vehicle: null },
  { slotId: 'C-HV-03', areaId: 'AREA-C', slotNumber: 'HV-303', type: 'HEAVY_VEHICLE', status: 'AVAILABLE', vehicle: null },
  { slotId: 'C-TW-01', areaId: 'AREA-C', slotNumber: 'TW-301', type: 'TWO_WHEELER', status: 'AVAILABLE', vehicle: null },
  { slotId: 'C-TW-02', areaId: 'AREA-C', slotNumber: 'TW-302', type: 'TWO_WHEELER', status: 'AVAILABLE', vehicle: null },

  // Area D
  { slotId: 'D-EV-01', areaId: 'AREA-D', slotNumber: 'EV-401', type: 'EV', status: 'OCCUPIED', vehicle: 'TN-33-ST-1616' },
  { slotId: 'D-EV-02', areaId: 'AREA-D', slotNumber: 'EV-402', type: 'EV', status: 'OCCUPIED', vehicle: 'TN-33-TU-1717' },
  { slotId: 'D-EV-03', areaId: 'AREA-D', slotNumber: 'EV-403', type: 'EV', status: 'AVAILABLE', vehicle: null },
  { slotId: 'D-EV-04', areaId: 'AREA-D', slotNumber: 'EV-404', type: 'EV', status: 'AVAILABLE', vehicle: null },
  { slotId: 'D-FW-01', areaId: 'AREA-D', slotNumber: 'FW-401', type: 'FOUR_WHEELER', status: 'AVAILABLE', vehicle: null },
  { slotId: 'D-FW-02', areaId: 'AREA-D', slotNumber: 'FW-402', type: 'FOUR_WHEELER', status: 'AVAILABLE', vehicle: null },
  { slotId: 'D-FW-03', areaId: 'AREA-D', slotNumber: 'FW-403', type: 'FOUR_WHEELER', status: 'AVAILABLE', vehicle: null },
  { slotId: 'D-FW-04', areaId: 'AREA-D', slotNumber: 'FW-404', type: 'FOUR_WHEELER', status: 'AVAILABLE', vehicle: null }
];

const DEFAULT_ACTIVE_TICKETS = [
  { ticketId: 1033, plate: 'TN-33-AB-1001', type: 'TWO_WHEELER', slotId: 'A-TW-01', entryTime: '2026-11-18 08:30:00', owner: 'Karthik Raja', phone: '9842111001' },
  { ticketId: 1034, plate: 'TN-33-BC-2002', type: 'TWO_WHEELER', slotId: 'A-TW-02', entryTime: '2026-11-18 08:45:00', owner: 'Suresh Kumar', phone: '9842111002' },
  { ticketId: 1035, plate: 'TN-33-FG-6006', type: 'FOUR_WHEELER', slotId: 'A-FW-01', entryTime: '2026-11-18 09:00:00', owner: 'Madhan sree', phone: '9842111054' },
  { ticketId: 1036, plate: 'TN-33-GH-7007', type: 'FOUR_WHEELER', slotId: 'A-FW-02', entryTime: '2026-11-18 09:15:00', owner: 'Venkatesh S', phone: '9842111007' },
  { ticketId: 1037, plate: 'TN-33-WX-2020', type: 'HEAVY_VEHICLE', slotId: 'A-HV-01', entryTime: '2026-11-18 09:30:00', owner: 'GreenLine Logistics', phone: '9842111020' },
  { ticketId: 1038, plate: 'TN-33-HJ-8008', type: 'FOUR_WHEELER', slotId: 'B-FW-01', entryTime: '2026-11-18 09:45:00', owner: 'Naveen Prashanth', phone: '9842111008' },
  { ticketId: 1039, plate: 'TN-33-JK-9009', type: 'FOUR_WHEELER', slotId: 'B-FW-02', entryTime: '2026-11-18 10:00:00', owner: 'Deepak Nathan', phone: '9842111009' },
  { ticketId: 1040, plate: 'TN-40-DD-4141', type: 'FOUR_WHEELER', slotId: 'C-FW-03', entryTime: '2026-11-18 10:10:00', owner: 'Senthil Nathan', phone: '9842111026' },
  { ticketId: 1041, plate: 'TN-33-ST-1616', type: 'EV', slotId: 'D-EV-01', entryTime: '2026-11-18 10:15:00', owner: 'Arunachalam M', phone: '9842111016' },
  { ticketId: 1042, plate: 'TN-33-TU-1717', type: 'EV', slotId: 'D-EV-02', entryTime: '2026-11-18 10:20:00', owner: 'Lavanya Ramesh', phone: '9842111017' }
];

// Fee Tariffs
const TARIFFS = {
  TWO_WHEELER: { name: 'Two-Wheeler', baseRate: 20.0, hourlyRate: 10.0 },
  FOUR_WHEELER: { name: 'Four-Wheeler', baseRate: 40.0, hourlyRate: 20.0 },
  EV: { name: 'Electric Vehicle (EV)', baseRate: 50.0, hourlyRate: 25.0 },
  HEAVY_VEHICLE: { name: 'Heavy Vehicle', baseRate: 80.0, hourlyRate: 40.0 }
};

// State Store
let state = {
  areas: JSON.parse(localStorage.getItem('spms_areas')) || DEFAULT_AREAS,
  slots: JSON.parse(localStorage.getItem('spms_slots')) || DEFAULT_SLOTS,
  activeTickets: JSON.parse(localStorage.getItem('spms_active_tickets')) || DEFAULT_ACTIVE_TICKETS,
  totalRevenue: parseFloat(localStorage.getItem('spms_revenue')) || 2682.50,
  nextTicketId: parseInt(localStorage.getItem('spms_next_ticket')) || 2005
};

function saveState() {
  localStorage.setItem('spms_areas', JSON.stringify(state.areas));
  localStorage.setItem('spms_slots', JSON.stringify(state.slots));
  localStorage.setItem('spms_active_tickets', JSON.stringify(state.activeTickets));
  localStorage.setItem('spms_revenue', state.totalRevenue.toString());
  localStorage.setItem('spms_next_ticket', state.nextTicketId.toString());
}

// App Initialization
document.addEventListener('DOMContentLoaded', () => {
  initLiveClock();
  initNavTabs();
  renderMetrics();
  renderParkingGrid();
  renderReports();
  initFormListeners();
});

// Live Clock
function initLiveClock() {
  const clockEl = document.getElementById('liveClock');
  if (!clockEl) return;
  setInterval(() => {
    const now = new Date();
    clockEl.textContent = now.toLocaleString('en-IN', { dateStyle: 'medium', timeStyle: 'medium' });
  }, 1000);
}

// Tab Switcher
function initNavTabs() {
  const tabButtons = document.querySelectorAll('.nav-tab-btn');
  const tabPanels = document.querySelectorAll('.tab-panel');

  tabButtons.forEach(btn => {
    btn.addEventListener('click', () => {
      tabButtons.forEach(b => b.classList.remove('active'));
      tabPanels.forEach(p => p.classList.remove('active'));

      btn.classList.add('active');
      const target = document.getElementById(btn.dataset.tab);
      if (target) target.classList.add('active');
    });
  });
}

// Render Top Metric Cards
function renderMetrics() {
  const totalSlots = state.slots.length;
  const occupiedSlots = state.slots.filter(s => s.status === 'OCCUPIED').length;
  const availableSlots = state.slots.filter(s => s.status === 'AVAILABLE').length;

  document.getElementById('metricTotalSlots').textContent = totalSlots;
  document.getElementById('metricOccupiedSlots').textContent = occupiedSlots;
  document.getElementById('metricAvailableSlots').textContent = availableSlots;
  document.getElementById('metricRevenue').textContent = '₹' + state.totalRevenue.toLocaleString('en-IN', { minimumFractionDigits: 2 });
}

// Render Interactive Parking Grid
function renderParkingGrid() {
  const container = document.getElementById('parkingGridContainer');
  if (!container) return;
  container.innerHTML = '';

  state.areas.forEach(area => {
    const areaSlots = state.slots.filter(s => s.areaId === area.areaId);
    const occupied = areaSlots.filter(s => s.status === 'OCCUPIED').length;
    const occPct = ((occupied / areaSlots.length) * 100).toFixed(1);

    const section = document.createElement('div');
    section.className = 'area-section';
    section.innerHTML = `
      <div class="area-header-info">
        <div>
          <h4>${area.areaName} (${area.areaId})</h4>
          <span style="font-size:0.8rem; color:#64748b;">📍 ${area.location}</span>
        </div>
        <div>
          <span class="badge" style="background:#e2e8f0; color:#334155;">Cap: ${areaSlots.length}</span>
          <span class="badge" style="background:${occPct > 75 ? '#fee2e2' : '#dcfce7'}; color:${occPct > 75 ? '#991b1b' : '#166534'};">
            ${occPct}% Occupied (${occupied}/${areaSlots.length})
          </span>
        </div>
      </div>
      <div class="slots-grid" id="slots-${area.areaId}"></div>
    `;
    container.appendChild(section);

    const slotsGrid = section.querySelector(`#slots-${area.areaId}`);
    areaSlots.forEach(slot => {
      const slotBox = document.createElement('div');
      slotBox.className = `slot-box ${slot.status}`;
      slotBox.title = `Slot: ${slot.slotId} | Type: ${slot.type} | Status: ${slot.status}`;
      slotBox.innerHTML = `
        <div class="slot-num">${slot.slotNumber}</div>
        <div class="slot-type">${slot.type.replace('_', ' ')}</div>
        <span class="slot-status-badge">${slot.status}</span>
        ${slot.vehicle ? `<div class="slot-vehicle-tag" title="${slot.vehicle}">🚗 ${slot.vehicle}</div>` : ''}
      `;
      slotsGrid.appendChild(slotBox);
    });
  });
}

// Form Handlers
function initFormListeners() {
  // Ingress Entry Form
  const entryForm = document.getElementById('entryForm');
  if (entryForm) {
    entryForm.addEventListener('submit', (e) => {
      e.preventDefault();
      const plate = document.getElementById('entryPlate').value.trim().toUpperCase();
      const type = document.getElementById('entryType').value;
      const owner = document.getElementById('entryOwner').value.trim() || 'Guest Customer';
      const phone = document.getElementById('entryPhone').value.trim();

      // Check if vehicle already parked
      if (state.activeTickets.some(t => t.plate === plate)) {
        showToast(`Vehicle ${plate} is already parked with an active ticket!`, 'error');
        return;
      }

      // Find available slot
      const targetSlot = state.slots.find(s => s.type === type && s.status === 'AVAILABLE');
      if (!targetSlot) {
        showToast(`No available slot for ${TARIFFS[type].name}! Facility full for this category.`, 'error');
        return;
      }

      // Allocate slot
      targetSlot.status = 'OCCUPIED';
      targetSlot.vehicle = plate;

      const ticketId = state.nextTicketId++;
      const now = new Date();
      const entryTimeStr = now.toISOString().replace('T', ' ').substring(0, 19);

      const newTicket = {
        ticketId,
        plate,
        type,
        slotId: targetSlot.slotId,
        entryTime: entryTimeStr,
        owner,
        phone
      };

      state.activeTickets.push(newTicket);
      saveState();

      renderMetrics();
      renderParkingGrid();
      renderReports();
      entryForm.reset();

      // Open ticket receipt modal
      openEntryTicketModal(newTicket);
      showToast(`Slot ${targetSlot.slotId} allocated successfully!`, 'success');
    });
  }

  // Egress Exit Form
  const exitForm = document.getElementById('exitForm');
  if (exitForm) {
    exitForm.addEventListener('submit', (e) => {
      e.preventDefault();
      const query = document.getElementById('exitQuery').value.trim().toUpperCase();
      
      let ticket = null;
      if (!isNaN(query)) {
        ticket = state.activeTickets.find(t => t.ticketId === parseInt(query));
      } else {
        ticket = state.activeTickets.find(t => t.plate === query);
      }

      if (!ticket) {
        showToast(`No active ticket found matching: ${query}`, 'error');
        return;
      }

      // Calculate fee
      const entryDate = new Date(ticket.entryTime);
      const exitDate = new Date();
      let durationHours = (exitDate - entryDate) / (1000 * 60 * 60);
      if (durationHours < 0.1) durationHours = 1.50; // Demo fallback

      const tariff = TARIFFS[ticket.type];
      let fee = 0.0;
      if (durationHours > 0.25) { // 15-min grace
        if (durationHours <= 2.0) {
          fee = tariff.baseRate;
        } else {
          const extra = Math.ceil(durationHours - 2.0);
          fee = tariff.baseRate + (extra * tariff.hourlyRate);
        }
      }

      // Free slot
      const slot = state.slots.find(s => s.slotId === ticket.slotId);
      if (slot) {
        slot.status = 'AVAILABLE';
        slot.vehicle = null;
      }

      // Remove from active tickets and add to revenue
      state.activeTickets = state.activeTickets.filter(t => t.ticketId !== ticket.ticketId);
      state.totalRevenue += fee;
      saveState();

      renderMetrics();
      renderParkingGrid();
      renderReports();
      exitForm.reset();

      openReceiptModal(ticket, durationHours, fee);
      showToast(`Exit recorded! Fee: ₹${fee.toFixed(2)}. Gate barrier opened.`, 'success');
    });
  }
}

// Render Reports Table
function renderReports() {
  const activeTableBody = document.getElementById('activeParkedTableBody');
  if (activeTableBody) {
    activeTableBody.innerHTML = '';
    if (state.activeTickets.length === 0) {
      activeTableBody.innerHTML = '<tr><td colspan="6" style="text-align:center;">No vehicles currently parked.</td></tr>';
    } else {
      state.activeTickets.forEach(t => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
          <td><strong>#${t.ticketId}</strong></td>
          <td><span class="badge" style="background:#e0f2fe; color:#0369a1;">${t.plate}</span></td>
          <td>${TARIFFS[t.type].name}</td>
          <td>${t.owner} (${t.phone})</td>
          <td><span class="badge" style="background:#fee2e2; color:#991b1b;">${t.slotId}</span></td>
          <td>${t.entryTime}</td>
        `;
        activeTableBody.appendChild(tr);
      });
    }
  }

  // Populate Exit Quick Select Dropdown
  const exitSelect = document.getElementById('exitQuickSelect');
  if (exitSelect) {
    exitSelect.innerHTML = '<option value="">-- Or choose from currently parked vehicles --</option>';
    state.activeTickets.forEach(t => {
      const opt = document.createElement('option');
      opt.value = t.ticketId;
      opt.textContent = `Ticket #${t.ticketId} | ${t.plate} (${t.slotId})`;
      exitSelect.appendChild(opt);
    });
    exitSelect.onchange = (e) => {
      if (e.target.value) {
        document.getElementById('exitQuery').value = e.target.value;
      }
    };
  }
}

// Modals
function openEntryTicketModal(ticket) {
  const modal = document.getElementById('ticketModal');
  const details = document.getElementById('ticketDetails');
  details.innerHTML = `
    <div class="receipt-header">
      <h3>PARKING ENTRY TICKET</h3>
      <p style="font-size:0.75rem; color:#64748b;">Velalar College Smart Parking Facility</p>
    </div>
    <div class="receipt-row"><span>Ticket ID:</span><strong>#${ticket.ticketId}</strong></div>
    <div class="receipt-row"><span>Vehicle Number:</span><strong>${ticket.plate}</strong></div>
    <div class="receipt-row"><span>Category:</span><span>${TARIFFS[ticket.type].name}</span></div>
    <div class="receipt-row"><span>Driver:</span><span>${ticket.owner}</span></div>
    <div class="receipt-row"><span>Assigned Bay:</span><strong style="color:#0284c7;">${ticket.slotId}</strong></div>
    <div class="receipt-row"><span>Entry Time:</span><span>${ticket.entryTime}</span></div>
    <div class="receipt-row"><span>Tariff:</span><span>Base ₹${TARIFFS[ticket.type].baseRate} (first 2 hrs)</span></div>
  `;
  modal.classList.add('active');
}

function openReceiptModal(ticket, durationHours, fee) {
  const modal = document.getElementById('receiptModal');
  const details = document.getElementById('receiptDetails');
  const hrs = Math.floor(durationHours);
  const mins = Math.round((durationHours - hrs) * 60);

  details.innerHTML = `
    <div class="receipt-header">
      <h3>PAYMENT RECEIPT</h3>
      <p style="font-size:0.75rem; color:#64748b;">Smart Parking Exit Invoice</p>
    </div>
    <div class="receipt-row"><span>Ticket ID:</span><strong>#${ticket.ticketId}</strong></div>
    <div class="receipt-row"><span>Vehicle Plate:</span><strong>${ticket.plate}</strong></div>
    <div class="receipt-row"><span>Category:</span><span>${TARIFFS[ticket.type].name}</span></div>
    <div class="receipt-row"><span>Released Slot:</span><span>${ticket.slotId}</span></div>
    <div class="receipt-row"><span>Stay Duration:</span><span>${hrs}h ${mins}m (${durationHours.toFixed(2)} hrs)</span></div>
    <div class="receipt-row total"><span>TOTAL PAID:</span><span>₹${fee.toFixed(2)}</span></div>
    <div class="receipt-row" style="margin-top:0.5rem; font-size:0.8rem; color:#16a34a;">
      <span>Status:</span><strong>PAID (Success)</strong>
    </div>
  `;
  modal.classList.add('active');
}

function closeModal(modalId) {
  document.getElementById(modalId).classList.remove('active');
}

function resetDemoData() {
  if (confirm('Reset entire parking system to initial university scenario seed data?')) {
    localStorage.clear();
    state = {
      areas: DEFAULT_AREAS,
      slots: DEFAULT_SLOTS,
      activeTickets: DEFAULT_ACTIVE_TICKETS,
      totalRevenue: 2682.50,
      nextTicketId: 2005
    };
    saveState();
    renderMetrics();
    renderParkingGrid();
    renderReports();
    showToast('Reset to default scenario seed data complete!', 'info');
  }
}

// Toast Notification
function showToast(message, type = 'info') {
  const container = document.getElementById('toastContainer');
  if (!container) return;

  const toast = document.createElement('div');
  toast.className = `toast ${type}`;
  toast.innerHTML = `<span>${type === 'success' ? '✅' : type === 'error' ? '❌' : 'ℹ️'}</span> ${message}`;

  container.appendChild(toast);
  setTimeout(() => {
    toast.style.opacity = '0';
    setTimeout(() => toast.remove(), 300);
  }, 3500);
}
