// SmartRide Application JavaScript Logic

let currentRole = 'passenger';
let allRides = [];

document.addEventListener('DOMContentLoaded', () => {
    initApp();
});

function initApp() {
    setupTabNavigation();
    setupFareEstimationListeners();
    setupBookingForm();
    setupSearchFilter();
    loadDashboardMetrics();
    loadRides();
    loadRecommendations();

    // Auto-refresh every 10 seconds for real-time demonstration
    setInterval(() => {
        loadRides(false);
        if (currentRole === 'admin') {
            loadDashboardMetrics();
        }
    }, 10000);
}

// Tab Switching
function setupTabNavigation() {
    const tabs = document.querySelectorAll('.nav-tab-btn');
    tabs.forEach(tab => {
        tab.addEventListener('click', () => {
            tabs.forEach(t => t.classList.remove('active'));
            tab.classList.add('active');

            currentRole = tab.dataset.role;
            document.querySelectorAll('.view-section').forEach(sec => sec.style.display = 'none');
            
            const targetSection = document.getElementById(`${currentRole}-section`);
            if (targetSection) {
                targetSection.style.display = 'block';
            }

            if (currentRole === 'admin') {
                loadDashboardMetrics();
            } else if (currentRole === 'passenger') {
                loadRecommendations();
            }
            renderViews();
        });
    });
}

// Fare Estimation
function setupFareEstimationListeners() {
    const sourceInput = document.getElementById('sourceLocation');
    const destInput = document.getElementById('destinationLocation');
    const seatsSelect = document.getElementById('requestedSeats');
    const vehicleSelect = document.getElementById('vehicleType');

    const updateEstimate = () => {
        const source = sourceInput?.value?.trim();
        const dest = destInput?.value?.trim();
        const seats = seatsSelect?.value || 1;
        const vehicle = vehicleSelect?.value || 'SEDAN';

        if (source && dest && source.length >= 2 && dest.length >= 2) {
            fetch(`/api/rides/estimate-fare?source=${encodeURIComponent(source)}&destination=${encodeURIComponent(dest)}&seats=${seats}&vehicleType=${vehicle}`)
                .then(res => res.json())
                .then(data => {
                    const fareBadge = document.getElementById('fareEstimateBadge');
                    if (fareBadge && data.estimatedFare) {
                        fareBadge.textContent = `$${parseFloat(data.estimatedFare).toFixed(2)}`;
                    }
                })
                .catch(() => {});
        }
    };

    sourceInput?.addEventListener('input', updateEstimate);
    destInput?.addEventListener('input', updateEstimate);
    seatsSelect?.addEventListener('change', updateEstimate);
    vehicleSelect?.addEventListener('change', updateEstimate);
}

// Booking Form
function setupBookingForm() {
    const form = document.getElementById('rideBookingForm');
    if (!form) return;

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const d = new Date(Date.now() + 15 * 60000);
        const pad = n => String(n).padStart(2, '0');
        const localDepartureTime = `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`;

        const payload = {
            passengerName: document.getElementById('passengerName').value,
            passengerPhone: document.getElementById('passengerPhone').value,
            sourceLocation: document.getElementById('sourceLocation').value,
            destinationLocation: document.getElementById('destinationLocation').value,
            departureTime: localDepartureTime,
            requestedSeats: parseInt(document.getElementById('requestedSeats').value, 10),
            vehicleType: document.getElementById('vehicleType').value
        };

        try {
            const res = await fetch('/api/rides', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            if (res.ok) {
                const created = await res.json();
                showToast(`Ride #${created.id} requested successfully!`, 'success');
                form.reset();
                document.getElementById('fareEstimateBadge').textContent = '$0.00';
                loadRides();
                loadRecommendations();
                loadDashboardMetrics();
            } else {
                const err = await res.json();
                showToast(err.message || 'Validation error requesting ride.', 'error');
            }
        } catch (err) {
            showToast('Failed to connect to backend server.', 'error');
        }
    });
}

// Load Rides
async function loadRides(showSpinner = true) {
    try {
        const res = await fetch('/api/rides');
        if (res.ok) {
            allRides = await res.json();
            renderViews();
        }
    } catch (err) {
        console.error('Failed to load rides:', err);
    }
}

// Render Content for Active Role
function renderViews() {
    renderPassengerRides();
    renderDriverRides();
    renderAdminTable(allRides);
}

// Passenger View
function renderPassengerRides() {
    const container = document.getElementById('passengerRidesList');
    if (!container) return;

    if (allRides.length === 0) {
        container.innerHTML = '<p class="text-muted" style="padding: 20px; text-align: center;">No active ride requests found. Book your first ride above!</p>';
        return;
    }

    container.innerHTML = allRides.map(ride => `
        <div class="card" style="margin-bottom: 12px; padding: 16px;">
            <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 8px;">
                <div>
                    <strong style="font-size: 1.05rem;">Ride #${ride.id}: ${escapeHtml(ride.sourceLocation)} ➔ ${escapeHtml(ride.destinationLocation)}</strong>
                    <div style="color: var(--text-muted); font-size: 0.85rem; margin-top: 4px;">
                        Passenger: ${escapeHtml(ride.passengerName)} | Seats: ${ride.requestedSeats} | ${ride.vehicleDisplayName}
                    </div>
                </div>
                <div style="text-align: right;">
                    <span class="badge badge-${ride.status}">${ride.status}</span>
                    <div style="font-weight: 700; color: #fff; margin-top: 4px;">$${parseFloat(ride.estimatedFare).toFixed(2)}</div>
                </div>
            </div>
            ${ride.driverName ? `
                <div style="background: rgba(255,255,255,0.03); border-radius: var(--radius-sm); padding: 8px 12px; margin: 8px 0; font-size: 0.85rem;">
                    🚗 Driver: <strong>${escapeHtml(ride.driverName)}</strong> (${escapeHtml(ride.driverVehicleNumber || 'Standard Fleet')})
                </div>
            ` : ''}
            <div style="display: flex; justify-content: flex-end; gap: 8px; margin-top: 10px;">
                <button class="btn btn-secondary btn-sm" onclick="openAuditModal(${ride.id})">🔍 Audit Timeline</button>
                ${(ride.status === 'REQUESTED' || ride.status === 'ACCEPTED') ? `
                    <button class="btn btn-danger btn-sm" onclick="cancelRide(${ride.id})">Cancel</button>
                ` : ''}
            </div>
        </div>
    `).join('');
}

// Driver View
function renderDriverRides() {
    const availableContainer = document.getElementById('driverAvailableList');
    const activeContainer = document.getElementById('driverActiveList');
    if (!availableContainer || !activeContainer) return;

    const driverName = document.getElementById('driverSelect')?.value || 'David Miller';

    const requested = allRides.filter(r => r.status === 'REQUESTED');
    const driverTrips = allRides.filter(r => (r.status === 'ACCEPTED' || r.status === 'DRIVER_ASSIGNED' || r.status === 'ONGOING'));

    // Available
    if (requested.length === 0) {
        availableContainer.innerHTML = '<p class="text-muted" style="padding: 16px; text-align: center;">No new unassigned requests in your queue.</p>';
    } else {
        availableContainer.innerHTML = requested.map(ride => `
            <div class="card" style="margin-bottom: 12px; padding: 16px;">
                <div style="display: flex; justify-content: space-between;">
                    <div>
                        <strong>#${ride.id}: ${escapeHtml(ride.sourceLocation)} ➔ ${escapeHtml(ride.destinationLocation)}</strong>
                        <div style="color: var(--text-muted); font-size: 0.85rem; margin-top: 4px;">
                            ${escapeHtml(ride.passengerName)} (${escapeHtml(ride.passengerPhone)}) | ${ride.vehicleDisplayName}
                        </div>
                    </div>
                    <div style="text-align: right;">
                        <span class="badge badge-REQUESTED">OPEN</span>
                        <div style="font-weight: 700; color: var(--accent-emerald); font-size: 1.1rem; margin-top: 4px;">$${parseFloat(ride.estimatedFare).toFixed(2)}</div>
                    </div>
                </div>
                <div style="display: flex; justify-content: flex-end; gap: 8px; margin-top: 12px;">
                    <button class="btn btn-secondary btn-sm" onclick="openAuditModal(${ride.id})">Timeline</button>
                    <button class="btn btn-primary btn-sm" id="accept-btn-${ride.id}" onclick="acceptRide(${ride.id}, '${driverName}')">✅ Accept Ride</button>
                </div>
            </div>
        `).join('');
    }

    // Driver Active
    if (driverTrips.length === 0) {
        activeContainer.innerHTML = '<p class="text-muted" style="padding: 16px; text-align: center;">No active trips in progress.</p>';
    } else {
        activeContainer.innerHTML = driverTrips.map(ride => {
            let nextActionBtn = '';
            if (ride.status === 'ACCEPTED') {
                nextActionBtn = `<button class="btn btn-warning btn-sm" onclick="transitionRideStatus(${ride.id}, 'DRIVER_ASSIGNED', 'Arrived at pickup location')">📍 Confirm Arrival</button>`;
            } else if (ride.status === 'DRIVER_ASSIGNED') {
                nextActionBtn = `<button class="btn btn-primary btn-sm" onclick="transitionRideStatus(${ride.id}, 'ONGOING', 'Trip started heading to destination')">🚀 Start Trip</button>`;
            } else if (ride.status === 'ONGOING') {
                nextActionBtn = `<button class="btn btn-success btn-sm" onclick="transitionRideStatus(${ride.id}, 'COMPLETED', 'Passenger reached safely')">🏁 Complete Ride</button>`;
            }

            return `
                <div class="card" style="margin-bottom: 12px; padding: 16px;">
                    <div style="display: flex; justify-content: space-between;">
                        <div>
                            <strong>#${ride.id}: ${escapeHtml(ride.sourceLocation)} ➔ ${escapeHtml(ride.destinationLocation)}</strong>
                            <div style="color: var(--text-muted); font-size: 0.85rem; margin-top: 4px;">
                                Passenger: ${escapeHtml(ride.passengerName)} | Driver: <strong>${escapeHtml(ride.driverName || 'Assigned')}</strong>
                            </div>
                        </div>
                        <div style="text-align: right;">
                            <span class="badge badge-${ride.status}">${ride.status}</span>
                            <div style="font-weight: 700; color: #fff; margin-top: 4px;">$${parseFloat(ride.estimatedFare).toFixed(2)}</div>
                        </div>
                    </div>
                    <div style="display: flex; justify-content: flex-end; gap: 8px; margin-top: 12px;">
                        <button class="btn btn-secondary btn-sm" onclick="openAuditModal(${ride.id})">Timeline</button>
                        ${nextActionBtn}
                    </div>
                </div>
            `;
        }).join('');
    }
}

// Admin Table View
function renderAdminTable(ridesToRender) {
    const tbody = document.getElementById('adminRidesTableBody');
    if (!tbody) return;

    if (ridesToRender.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" style="text-align:center; padding: 20px; color: var(--text-muted);">No rides match query.</td></tr>';
        return;
    }

    tbody.innerHTML = ridesToRender.map(ride => `
        <tr>
            <td><strong>#${ride.id}</strong></td>
            <td>
                <div>${escapeHtml(ride.passengerName)}</div>
                <small style="color: var(--text-muted);">${escapeHtml(ride.passengerPhone)}</small>
            </td>
            <td>${escapeHtml(ride.sourceLocation)} ➔ ${escapeHtml(ride.destinationLocation)}</td>
            <td>${ride.vehicleDisplayName} (${ride.requestedSeats} seats)</td>
            <td><strong>$${parseFloat(ride.estimatedFare).toFixed(2)}</strong></td>
            <td><span class="badge badge-${ride.status}">${ride.status}</span></td>
            <td>
                <div style="display: flex; gap: 6px;">
                    <button class="btn btn-secondary btn-sm" onclick="openAuditModal(${ride.id})">🔍 Audit</button>
                    ${ride.status !== 'COMPLETED' && ride.status !== 'CANCELLED' ? `
                        <button class="btn btn-danger btn-sm" onclick="cancelRide(${ride.id})">Cancel</button>
                    ` : ''}
                </div>
            </td>
        </tr>
    `).join('');
}

// Search and Filter
function setupSearchFilter() {
    const searchInput = document.getElementById('adminSearchInput');
    const statusFilter = document.getElementById('adminStatusFilter');

    const filterHandler = () => {
        const query = searchInput?.value?.toLowerCase().trim() || '';
        const status = statusFilter?.value || 'ALL';

        const filtered = allRides.filter(r => {
            const matchesQuery = !query || 
                r.passengerName.toLowerCase().includes(query) ||
                r.sourceLocation.toLowerCase().includes(query) ||
                r.destinationLocation.toLowerCase().includes(query) ||
                (r.driverName && r.driverName.toLowerCase().includes(query));
            const matchesStatus = status === 'ALL' || r.status === status;
            return matchesQuery && matchesStatus;
        });

        renderAdminTable(filtered);
    };

    searchInput?.addEventListener('input', filterHandler);
    statusFilter?.addEventListener('change', filterHandler);
}

// Status Transitions
async function acceptRide(rideId, driverName) {
    const payload = {
        targetStatus: 'ACCEPTED',
        actorRole: 'DRIVER',
        actorName: driverName,
        driverName: driverName,
        driverVehicleNumber: 'FLEET-' + (Math.floor(Math.random() * 8999) + 1000),
        notes: `Driver ${driverName} accepted the ride booking.`
    };

    await updateRideStatus(rideId, payload);
}

async function transitionRideStatus(rideId, targetStatus, notes) {
    const driverName = document.getElementById('driverSelect')?.value || 'David Miller';
    const payload = {
        targetStatus: targetStatus,
        actorRole: 'DRIVER',
        actorName: driverName,
        notes: notes
    };

    await updateRideStatus(rideId, payload);
}

async function cancelRide(rideId) {
    if (!confirm(`Are you sure you want to cancel Ride #${rideId}?`)) return;

    const payload = {
        targetStatus: 'CANCELLED',
        actorRole: currentRole.toUpperCase(),
        actorName: currentRole === 'passenger' ? 'Passenger' : 'Admin User',
        notes: `Ride cancelled by ${currentRole}.`
    };

    await updateRideStatus(rideId, payload);
}

async function updateRideStatus(rideId, payload) {
    try {
        const res = await fetch(`/api/rides/${rideId}/status`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (res.ok) {
            const updated = await res.json();
            showToast(`Ride #${rideId} status updated to ${updated.status}`, 'info');
            loadRides();
            loadDashboardMetrics();
        } else {
            const err = await res.json();
            showToast(err.message || 'Status transition rejected.', 'error');
        }
    } catch (err) {
        showToast('Failed to update ride status.', 'error');
    }
}

// Smart Recommendations
async function loadRecommendations() {
    const container = document.getElementById('recommendationsList');
    if (!container) return;

    try {
        const res = await fetch('/api/rides/recommendations');
        if (res.ok) {
            const recs = await res.json();
            if (recs.length === 0) {
                container.innerHTML = '<p class="text-muted" style="padding: 12px; font-size: 0.9rem;">No shared route recommendations currently available.</p>';
                return;
            }

            container.innerHTML = recs.map(rec => `
                <div class="rec-card">
                    <div>
                        <div class="rec-title">${escapeHtml(rec.sourceLocation)} ➔ ${escapeHtml(rec.destinationLocation)}</div>
                        <div class="rec-meta">
                            Match Score: <strong>${rec.matchScorePercent}%</strong> (${rec.matchReason}) | Departs: ${rec.departureTimeFormatted}
                        </div>
                    </div>
                    <div style="display: flex; align-items: center; gap: 12px;">
                        <span class="rec-savings">Save $${parseFloat(rec.potentialSavings).toFixed(2)}</span>
                        <button class="btn btn-primary btn-sm" onclick="joinRecommendedRide(${rec.matchedRideId})">Join Shared Ride</button>
                    </div>
                </div>
            `).join('');
        }
    } catch (err) {
        console.error('Recommendations load error:', err);
    }
}

function joinRecommendedRide(rideId) {
    showToast(`Joined shared ride corridor for Ride #${rideId}!`, 'success');
}

// Dashboard Metrics
async function loadDashboardMetrics() {
    try {
        const res = await fetch('/api/admin/metrics');
        if (res.ok) {
            const m = await res.json();
            document.getElementById('metricTotalRides').textContent = m.totalRides;
            document.getElementById('metricActiveRides').textContent = m.activeRides;
            document.getElementById('metricCompletedRides').textContent = m.completedRides;
            document.getElementById('metricCancelledRides').textContent = m.cancelledRides;
            document.getElementById('metricUtilization').textContent = `${m.fleetUtilizationRate}%`;
            if (document.getElementById('metricRevenue')) {
                document.getElementById('metricRevenue').textContent = `$${parseFloat(m.totalRevenue).toFixed(2)}`;
            }
        }
    } catch (err) {
        console.error('Failed to load metrics:', err);
    }
}

// Audit Modal
async function openAuditModal(rideId) {
    const modal = document.getElementById('auditModal');
    const timelineContainer = document.getElementById('auditTimelineContainer');
    const modalTitle = document.getElementById('auditModalTitle');

    if (!modal || !timelineContainer) return;

    modalTitle.textContent = `Activity & Audit Trail — Ride #${rideId}`;
    timelineContainer.innerHTML = '<p class="text-muted">Loading audit log events...</p>';
    modal.classList.add('active');

    try {
        const res = await fetch(`/api/rides/${rideId}`);
        if (res.ok) {
            const ride = await res.json();
            if (!ride.auditTrail || ride.auditTrail.length === 0) {
                timelineContainer.innerHTML = '<p class="text-muted">No audit events recorded yet.</p>';
                return;
            }

            timelineContainer.innerHTML = `
                <div class="timeline">
                    ${ride.auditTrail.map(log => `
                        <div class="timeline-item">
                            <div class="timeline-dot"></div>
                            <div class="timeline-content">
                                <div class="timeline-header">
                                    <span class="timeline-role">[${log.actorRole}] ${escapeHtml(log.actorName)}</span>
                                    <span class="timeline-time">${new Date(log.timestamp).toLocaleTimeString()}</span>
                                </div>
                                <div style="margin: 4px 0;">
                                    ${log.previousStatus ? `<span class="badge badge-${log.previousStatus}">${log.previousStatus}</span> ➔ ` : ''}
                                    <span class="badge badge-${log.newStatus}">${log.newStatus}</span>
                                </div>
                                <div class="timeline-note">${escapeHtml(log.notes)}</div>
                            </div>
                        </div>
                    `).join('')}
                </div>
            `;
        }
    } catch (err) {
        timelineContainer.innerHTML = '<p style="color: var(--accent-rose);">Error retrieving audit logs.</p>';
    }
}

function closeAuditModal() {
    const modal = document.getElementById('auditModal');
    if (modal) modal.classList.remove('active');
}

// Reset Seed Data
async function resetSeedData() {
    if (!confirm('Reset database to default seed state?')) return;
    try {
        const res = await fetch('/api/admin/reset-seed', { method: 'POST' });
        if (res.ok) {
            showToast('Seed demonstration data successfully restored!', 'success');
            loadRides();
            loadDashboardMetrics();
            loadRecommendations();
        }
    } catch (err) {
        showToast('Failed to reset seed data.', 'error');
    }
}

// Toast Notifications
function showToast(msg, type = 'info') {
    const container = document.getElementById('toastContainer') || createToastContainer();
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.innerHTML = `<span>${escapeHtml(msg)}</span>`;
    container.appendChild(toast);

    setTimeout(() => {
        toast.style.opacity = '0';
        setTimeout(() => toast.remove(), 300);
    }, 4000);
}

function createToastContainer() {
    const c = document.createElement('div');
    c.id = 'toastContainer';
    c.className = 'toast-container';
    document.body.appendChild(c);
    return c;
}

function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;").replace(/'/g, "&#039;");
}
