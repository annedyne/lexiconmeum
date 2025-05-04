import { config } from './config.js';
const searchURI = `${config.apiBaseUrl}/search/`;
const prefixURI = "prefix?prefix=";
const suffixURI = "suffix?suffix=";
const queryCharMin = 2
const isSuffixSearch = document.getElementById("suffix-search")

const statusBar = document.getElementById("status-bar")
const wordLookupInput = document.getElementById("word-lookup-input");
const wordSuggestionsBox = document.getElementById("word-suggestions");
wordSuggestionsBox.style.display = "none";

wordLookupInput.addEventListener("input", async () => {
    const query = wordLookupInput.value.trim();
    wordSuggestionsBox.innerHTML = "";

    // wait for at least 2 letters before fetching suggestions
    if (query.length < queryCharMin) return;

    try {
        let words = await fetchWordSuggestions(query);
        if (Array.isArray(words) && words.length > 0) {
            buildWordSuggestionBox(words);
        }
    } catch (err) {
        let message = "Error fetching suggestions: ";
        console.error(message, err);
        setStatus(message + query);
    }
});

//hide suggestions when clicking outside the box
document.addEventListener("click", (e) => {
    if (!wordLookupInput.contains(e.target) && e.target !== wordLookupInput) {
        wordSuggestionsBox.style.display = "none";
    }
});

async function fetchWordSuggestions(query){
    const subAPI = isSuffixSearch.checked ? suffixURI : prefixURI
    const uri = searchURI + subAPI + encodeURIComponent(query);
    const res = await fetch( uri);
    return await res.json();
}

function buildWordSuggestionBox(words){
    wordSuggestionsBox.style.display = "block"
    words.forEach(word => {
        const item = document.createElement("div");
        item.textContent = word;
        item.addEventListener("click", () => {
            wordLookupInput.value = word;
            wordSuggestionsBox.innerHTML = ""; // hide suggestions
            fetchWordDetail(word);
        });
        wordSuggestionsBox.appendChild(item);
    });
}

function fetchWordDetail(word){
    setStatus("Fetching definition for: " + word);
}

function setStatus(message){
    statusBar.textContent = message;
}

