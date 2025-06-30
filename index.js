import { config } from './config.js';
import {renderDeclensionTable} from "./renderDeclensionTable.js"
import {renderConjugationTable} from "./renderConjugationTable.js"


const searchURI = `${config.apiBaseUrl}/search/`;
const prefixURI = "prefix?prefix=";
const suffixURI = "suffix?suffix=";
const declensionDetailURI = `${config.apiBaseUrl}/lexeme/detail/declension?lexemeId=`
const conjugationDetailURI = `${config.apiBaseUrl}/lexeme/detail/conjugation?lexemeId=`
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
        console.log(words);
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
    words.forEach(wordObj => {

        const { word, lexemeId, grammaticalPosition } = wordObj;
        const item = document.createElement("div");

        console.log("lemma: " + word);
        item.textContent = word;
        item.addEventListener("click", () => {
            wordLookupInput.value = lexemeId;
            wordSuggestionsBox.innerHTML = ""; // hide suggestions
            buildWordDetailTable(word, lexemeId, grammaticalPosition);
        });
        wordSuggestionsBox.appendChild(item);
    });
}



async function buildWordDetailTable(lemma, lexemeId, grammaticalPosition ){
    try {
        if(grammaticalPosition === "NOUN"){
            console.log(lemma);
            let wordDetailData = await fetchWordDetailData(lexemeId);

            if (wordDetailData) {
                renderDeclensionTable(wordDetailData);
            }
        }
        else if(grammaticalPosition === "VERB"){
            let wordDetailData = await fetchConjugationDetailData(lexemeId);
            const  activeMoods = wordDetailData.conjugationTableDTOList.filter(
                d => d.voice === "ACTIVE"
            );
            const container = document.getElementById("tables-container");
            container.innerHTML = ""; // Clear once at the top

            // Add a voice-level heading once at the top
            const voiceHeader = document.createElement("h2");
            voiceHeader.textContent = "Voice: ACTIVE";
            container.appendChild(voiceHeader);

            if (activeMoods) {
                activeMoods.forEach(renderConjugationTable);
            }
        }

    } catch (err) {
        let message = "Error fetching suggestions: ";
        console.error(message, err);
        setStatus(message + lemma);
    }
}



async function fetchWordDetailData(word) {

    const uri = declensionDetailURI + encodeURIComponent(word);
    const res = await fetch(uri);
    let jsn = await res.json();
    console.log(jsn);
    return jsn;
}

async function fetchConjugationDetailData(word) {

    const uri = conjugationDetailURI + encodeURIComponent(word);
    const res = await fetch(uri);
    let jsn = await res.json();
    console.log(jsn);
    return jsn;
}


function setStatus(message){
    statusBar.textContent = message;
}



