export function renderDeclensionTable(data) {
    const tableData = data.table;
    const cases = Object.keys(tableData.SINGULAR); // Assume both singular and plural have same cases

    const table = document.createElement("table");
    table.classList.add("declension-table");

    // Create table header
    const thead = document.createElement("thead");
    const headerRow = document.createElement("tr");
    ["Case", "Singular", "Plural"].forEach(heading => {
        const th = document.createElement("th");
        th.textContent = heading;
        headerRow.appendChild(th);
    });
    thead.appendChild(headerRow);
    table.appendChild(thead);

    // Create table body
    const tbody = document.createElement("tbody");
    cases.forEach(c => {
        const row = document.createElement("tr");

        const caseCell = document.createElement("td");
        caseCell.textContent = capitalize(c.toLowerCase()); // Optional: nicer formatting
        row.appendChild(caseCell);

        const singularCell = document.createElement("td");
        singularCell.textContent = tableData.SINGULAR[c] || "";
        row.appendChild(singularCell);

        const pluralCell = document.createElement("td");
        pluralCell.textContent = tableData.PLURAL[c] || "";
        row.appendChild(pluralCell);

        tbody.appendChild(row);
    });
    table.appendChild(tbody);

    // Replace existing content
    const container = document.getElementById("tables-container");
    container.innerHTML = "";
    container.appendChild(table);
}

// Helper to capitalize strings
function capitalize(str) {
    return str.charAt(0).toUpperCase() + str.slice(1);
}