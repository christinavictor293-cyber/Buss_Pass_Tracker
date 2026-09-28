const reviewForm = document.getElementById("reviewForm");
const decisionSelect = document.getElementById("reviewDecision");
const reasonWrap = document.getElementById("reasonWrap");
const monthsWrap = document.getElementById("monthsWrap");

/* ---------- show the right fields for the chosen decision ---------- */
function updateReviewFields() {
  const rejecting = decisionSelect.value === "REJECT";
  reasonWrap.classList.toggle("hidden", !rejecting);
  monthsWrap.classList.toggle("hidden", rejecting);
}
decisionSelect.addEventListener("change", updateReviewFields);
updateReviewFields();

/* ---------- approve / reject ---------- */
reviewForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  const rejecting = decisionSelect.value === "REJECT";
  const id = document.getElementById("reviewId").value;

  const body = {
    decision: decisionSelect.value,
    remark: document.getElementById("reviewRemark").value.trim(),
  };
  if (rejecting) {
    // Sent as typed. The backend enforces "a rejection needs a reason".
    body.rejectionReason = document.getElementById("reviewReason").value;
  } else {
    body.validityMonths = Number(document.getElementById("reviewMonths").value);
  }

  try {
    const result = await api("/admin/applications/" + id + "/review", {
      method: "PUT",
      body: JSON.stringify(body),
    });
    document.getElementById("reviewResult").innerHTML = applicationCard(result);
    showToast("Application #" + result.id + " is now " + result.status);
  } catch (err) {
    showToast(err.message, "error");
  }
});

/* ---------- expiring passes ---------- */
document.getElementById("loadExpiring").addEventListener("click", async () => {
  const box = document.getElementById("expiringResults");
  try {
    const list = await api("/admin/passes/expiring");
    box.innerHTML =
      list.length === 0
        ? '<p class="empty">No passes expire in the next 30 days.</p>'
        : list.map((a) => applicationCard(a)).join("");
  } catch (err) {
    box.innerHTML = "";
    showToast(err.message, "error");
  }
});

/* ---------- routes ---------- */
async function loadRoutesTable() {
  const body = document.getElementById("routesBody");
  try {
    const routes = await api("/routes");
    body.innerHTML =
      routes.length === 0
        ? '<tr><td colspan="6" class="empty">No routes yet</td></tr>'
        : routes
            .map((r) =>
              "<tr><td>" + r.id + "</td><td>" + escapeHtml(r.routeCode) + "</td><td>" +
              escapeHtml(r.routeName) + "</td><td>" + escapeHtml(r.startPoint) + "</td><td>" +
              escapeHtml(r.endPoint) + "</td><td>" +
              r.boardingPoints.map(escapeHtml).join(", ") + "</td></tr>")
            .join("");
  } catch (err) {
    showToast(err.message, "error");
  }
}

document.getElementById("routeForm").addEventListener("submit", async (event) => {
  event.preventDefault();
  const stops = document
    .getElementById("routeStops")
    .value.split(",")
    .map((s) => s.trim())
    .filter((s) => s.length > 0);

  try {
    const route = await api("/routes", {
      method: "POST",
      body: JSON.stringify({
        routeCode: document.getElementById("routeCode").value.trim(),
        routeName: document.getElementById("routeName").value.trim(),
        startPoint: document.getElementById("routeStart").value.trim(),
        endPoint: document.getElementById("routeEnd").value.trim(),
        boardingPoints: stops,
      }),
    });
    showToast("Route " + route.routeCode + " added");
    event.target.reset();
    loadRoutesTable();
  } catch (err) {
    showToast(err.message, "error");
  }
});

loadRoutesTable();