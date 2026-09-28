const API_BASE = "/api";

/**
 * Calls the backend. Returns parsed JSON (or null for 204).
 * Throws an Error whose message is the backend's error message.
 */
async function api(path, options = {}) {
  const response = await fetch(API_BASE + path, {
    headers: { "Content-Type": "application/json" },
    ...options,
  });

  if (response.status === 204) return null;

  let data = null;
  try {
    data = await response.json();
  } catch (e) {
    // response had no JSON body
  }

  if (!response.ok) {
    const message = (data && data.message) || "Request failed (" + response.status + ")";
    throw new Error(message);
  }
  return data;
}

let toastTimer;
function showToast(message, type = "success") {
  const toast = document.getElementById("toast");
  toast.textContent = message;
  toast.className = "toast " + type;
  clearTimeout(toastTimer);
  toastTimer = setTimeout(() => toast.classList.add("hidden"), 4500);
}

function escapeHtml(value) {
  if (value === null || value === undefined) return "";
  return String(value)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;");
}

function formatDate(value) {
  return value ? value : "-";
}

/** Builds the HTML for one application. Set withCancel to show a cancel button on PENDING ones. */
function applicationCard(app, withCancel = false) {
  const cancelButton =
    withCancel && app.status === "PENDING"
      ? '<div class="app-actions"><button class="btn btn-danger btn-small" data-cancel="' +
        app.id + '">Cancel application</button></div>'
      : "";

  return (
    '<article class="app-card ' + app.status.toLowerCase() + '">' +
      "<header>" +
        "<strong>Application #" + app.id + "</strong>" +
        '<span class="badge ' + app.status + '">' + app.status + "</span>" +
      "</header>" +
      "<dl>" +
        "<dt>Student</dt><dd>" + escapeHtml(app.studentName) + " (ID " + app.studentId + ")</dd>" +
        "<dt>Route</dt><dd>" + escapeHtml(app.routeCode) + "</dd>" +
        "<dt>Boarding point</dt><dd>" + escapeHtml(app.boardingPoint) + "</dd>" +
        "<dt>Photo reference</dt><dd>" + escapeHtml(app.photoRef) + "</dd>" +
        "<dt>Pass number</dt><dd>" + formatDate(escapeHtml(app.passNumber)) + "</dd>" +
        "<dt>Valid from</dt><dd>" + formatDate(app.validFrom) + "</dd>" +
        "<dt>Valid until</dt><dd>" + formatDate(app.validUntil) + "</dd>" +
        "<dt>Admin remark</dt><dd>" + formatDate(escapeHtml(app.adminRemark)) + "</dd>" +
        "<dt>Rejection reason</dt><dd>" + formatDate(escapeHtml(app.rejectionReason)) + "</dd>" +
      "</dl>" +
      cancelButton +
    "</article>"
  );
}