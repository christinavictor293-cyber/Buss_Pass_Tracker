let routes = [];

const registerForm = document.getElementById("registerForm");
const applyForm = document.getElementById("applyForm");
const routeSelect = document.getElementById("applyRoute");
const boardingSelect = document.getElementById("applyBoarding");
const resultsBox = document.getElementById("applicationResults");

/* ---------- load routes into the dropdown ---------- */
async function loadRoutes() {
  try {
    routes = await api("/routes");
    if (routes.length === 0) {
      routeSelect.innerHTML = '<option value="">No routes available yet</option>';
      return;
    }
    routeSelect.innerHTML =
      '<option value="">Select a route</option>' +
      routes
        .map((r) => '<option value="' + r.id + '">' +
          escapeHtml(r.routeCode) + " - " + escapeHtml(r.routeName) + "</option>")
        .join("");
  } catch (err) {
    routeSelect.innerHTML = '<option value="">Could not load routes</option>';
    showToast(err.message, "error");
  }
}

routeSelect.addEventListener("change", () => {
  const route = routes.find((r) => String(r.id) === routeSelect.value);
  if (!route) {
    boardingSelect.innerHTML = '<option value="">Select a route first</option>';
    boardingSelect.disabled = true;
    return;
  }
  boardingSelect.innerHTML = route.boardingPoints
    .map((p) => '<option value="' + escapeHtml(p) + '">' + escapeHtml(p) + "</option>")
    .join("");
  boardingSelect.disabled = false;
});

/* ---------- register student ---------- */
registerForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  try {
    const student = await api("/students", {
      method: "POST",
      body: JSON.stringify({
        name: document.getElementById("regName").value.trim(),
        rollNumber: document.getElementById("regRoll").value.trim(),
        email: document.getElementById("regEmail").value.trim(),
        department: document.getElementById("regDept").value.trim(),
      }),
    });
    document.getElementById("registerResult").innerHTML =
      "Registered! Your <strong>Student ID is " + student.id + "</strong>. Keep it to apply and track.";
    document.getElementById("applyStudentId").value = student.id;
    document.getElementById("trackStudentId").value = student.id;
    registerForm.reset();
    showToast("Registered successfully. Student ID: " + student.id);
  } catch (err) {
    showToast(err.message, "error");
  }
});

/* ---------- submit application ---------- */
applyForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  try {
    const application = await api("/applications", {
      method: "POST",
      body: JSON.stringify({
        studentId: Number(document.getElementById("applyStudentId").value),
        routeId: Number(routeSelect.value),
        boardingPoint: boardingSelect.value,
        photoRef: document.getElementById("applyPhoto").value.trim(),
      }),
    });
    showToast("Application submitted. Your Application ID is " + application.id);
    document.getElementById("trackApplicationId").value = application.id;
    resultsBox.innerHTML = applicationCard(application, true);
  } catch (err) {
    showToast(err.message, "error"); // e.g. "already has an active pass or a pending application"
  }
});

/* ---------- track one application ---------- */
document.getElementById("trackByIdForm").addEventListener("submit", async (event) => {
  event.preventDefault();
  try {
    const id = document.getElementById("trackApplicationId").value;
    const application = await api("/applications/" + id);
    resultsBox.innerHTML = applicationCard(application, true);
  } catch (err) {
    resultsBox.innerHTML = "";
    showToast(err.message, "error");
  }
});

/* ---------- list all applications of a student ---------- */
document.getElementById("trackByStudentForm").addEventListener("submit", async (event) => {
  event.preventDefault();
  try {
    const studentId = document.getElementById("trackStudentId").value;
    const list = await api("/students/" + studentId + "/applications");
    resultsBox.innerHTML =
      list.length === 0
        ? '<p class="empty">No applications found for this student.</p>'
        : list.map((a) => applicationCard(a, true)).join("");
  } catch (err) {
    resultsBox.innerHTML = "";
    showToast(err.message, "error");
  }
});

/* ---------- cancel a pending application ---------- */
resultsBox.addEventListener("click", async (event) => {
  const id = event.target.dataset.cancel;
  if (!id) return;
  if (!confirm("Cancel application #" + id + "?")) return;
  try {
    await api("/applications/" + id, { method: "DELETE" });
    showToast("Application #" + id + " cancelled");
    resultsBox.innerHTML = "";
  } catch (err) {
    showToast(err.message, "error");
  }
});

loadRoutes();