import { config } from './config.js';
import {renderDeclensionTable} from "./renderDeclensionTable.js"


const searchURI = `${config.apiBaseUrl}/search/`;
const prefixURI = "prefix?prefix=";
const suffixURI = "suffix?suffix=";
const declensionDetailURI = `${config.apiBaseUrl}/lexeme/detail/declension?lexemeId=`
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
        let indexOfDelimiter = word.indexOf(":");
        let viewWord = word.substring(0, indexOfDelimiter);
        console.log("after substring" + word);
        item.textContent = viewWord;
        item.addEventListener("click", () => {
            wordLookupInput.value = viewWord;
            wordSuggestionsBox.innerHTML = ""; // hide suggestions
            buildWordDetailTable(word);
        });
        wordSuggestionsBox.appendChild(item);
    });
}

async function fetchWordDetailData(word) {
    let index = word.indexOf(":");
    word = word.substring(index + 1).trim();
    console.log(word);
    const uri = declensionDetailURI + encodeURIComponent(word);
    const res = await fetch(uri);
    let jsn = await res.json();
    console.log(jsn);
    return jsn;
}

async function buildWordDetailTable(query){
    try {
        let wordDetailData = await fetchWordDetailData(query);
        if (wordDetailData) {
            renderDeclensionTable(wordDetailData);
        }
    } catch (err) {
        let message = "Error fetching suggestions: ";
        console.error(message, err);
        setStatus(message + query);
    }
}


function setStatus(message){
    statusBar.textContent = message;
}



