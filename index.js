const ulEl = document.getElementById("ul-el");

const prefixUrl = "http://localhost:8080/api/search/prefix?prefix=";

async function getWordsWithPrefix(prefix) {
    console.log(prefix);

    const url = prefixUrl + prefix;
    console.log(url);
    const response = await fetch(url);
    const data = await response.json();
    console.log(data);
    return data;
}

const suggestions = await getWordsWithPrefix('ama');
display(suggestions);

function display(words) {
    let listItems = "";
    words.forEach((word) => {
       listItems += `<li> ${word} </li>`
    })

    ulEl.innerHTML = listItems;
}

