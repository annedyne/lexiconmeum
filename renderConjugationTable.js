
export function renderConjugationTable(data) {

    const { voice, mood, tenses } = data;

    // Create a table
    const table = document.createElement("table");
    table.classList.add("latin-table");

    // Create header row that spans both columns
    const thead = document.createElement("thead");
    const headerRow = document.createElement("tr");
    const headerCell = document.createElement("th");
    headerCell.colSpan = 2;
    headerCell.className = "header";
    headerCell.textContent = `Voice: ${voice}`;
    headerRow.appendChild(headerCell);
    thead.appendChild(headerRow);
    table.appendChild(thead);

    // Table body with tenses in pairs
    const tbody = document.createElement("tbody");

    for (let i = 0; i < tenses.length; i += 2) {
        const left = tenses[i];
        const right = tenses[i + 1];

        // Build header row for tense names
        const headerRow = tbody.insertRow();
        const leftHeader = headerRow.insertCell();
        leftHeader.colSpan = 1;
        leftHeader.className = "tense-header";
        leftHeader.textContent = `${mood} ${left.defaultName}`

        const rightHeader = headerRow.insertCell();
        rightHeader.colSpan = 1;
        rightHeader.className = "tense-header";
        rightHeader.textContent =  `${mood} ${right.defaultName}`

        // Compute max form count
        const maxRows = Math.max(left.forms.length, right?.forms.length || 0);

        // Build one row per form index
        for (let j = 0; j < maxRows; j++) {
            const formRow = tbody.insertRow();
            const leftForm = left.forms[j] || "";   // pad if undefined
            const rightForm = right?.forms[j] || ""; // pad if no right tense

            formRow.insertCell().innerHTML = leftForm;
            formRow.insertCell().innerHTML = rightForm;
        }
    }

    table.appendChild(tbody)
    const container = document.getElementById("tables-container");
    container.appendChild(table);


    function capitalize(str) {
        return str.charAt(0).toUpperCase() + str.slice(1);
    }
}
