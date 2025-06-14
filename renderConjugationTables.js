
/*
const verbData = {
    "active": {
    "indicative": {
        "imperfect": {
            "first-person": {
                "singular": "amābam",
                    "plural": "amābāmus"
            },
            "second-person": {
                "singular": "amābās",
                    "plural": "amābātis"
            },
            "third-person": {
                "singular": "amābat",
                    "plural": "amābant"
            }
        }
    }
}
}
 */

/*
const nounData = {
    "active": {
    "indicative": {
        "imperfect": {
            "first-person": {
                "singular": "amābam",
                    "plural": "amābāmus"
            },
            "second-person": {
                "singular": "amābās",
                    "plural": "amābātis"
            },
            "third-person": {
                "singular": "amābat",
                    "plural": "amābant"
            }
        }
    }
}
}
 */
export function renderConjugationTables(data) {
    const container = document.getElementById("tables-container");
    container.innerHTML = ""; // Clear existing content

    const persons = ["first-person", "second-person", "third-person"];
    const numbers = ["singular", "plural"];
    console.log(`This is the data: ${JSON.stringify(data, null, 2)}`);
    for (let voice in data) {
        for (let mood in data[voice]) {
            for (let tense in data[voice][mood]) {
                // Create a table and header
                const sectionTitle = `${capitalize(voice)} — ${capitalize(mood)} ${capitalize(tense)}`;
                const table = document.createElement("table");
                table.classList.add("latin-table");

                const caption = document.createElement("caption");
                caption.textContent = sectionTitle;
                table.appendChild(caption);

                // Create header row
                const thead = document.createElement("thead");
                const headerRow = document.createElement("tr");
                headerRow.innerHTML = `<th></th><th>Singular</th><th>Plural</th>`;
                thead.appendChild(headerRow);
                table.appendChild(thead);

                // Create table body
                const tbody = document.createElement("tbody");
                for (let person of persons) {
                    const row = document.createElement("tr");
                    const label = capitalize(person.replace("-person", " person"));
                    row.innerHTML = `<td>${label}</td>`;

                    for (let number of numbers) {
                        const form = data[voice][mood][tense][person]?.[number] || "";
                        row.innerHTML += `<td>${form}</td>`;
                    }

                    tbody.appendChild(row);
                }

                table.appendChild(tbody);
                container.appendChild(table);
            }
        }
    }
}



// Helper to capitalize strings
function capitalize(str) {
    return str.charAt(0).toUpperCase() + str.slice(1);
}