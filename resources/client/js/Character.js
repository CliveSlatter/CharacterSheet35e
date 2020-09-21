function PageLoad(){
    characterList();
}

function characterList(){
    document.getElementById("characterSort").addEventListener("change", characterList);

    fetch('/character/list', {method: 'get'}
    ).then(response => response.json()
    ).then(characters => {
        if (data.hasOwnProperty('error')) {
            alert(data.error);
        }else if (data.hasOwnProperty('characters') && data.characters.length > 0) {
            let order = document.getElementById("characterSort").value;

            data.characters.sort(function (a, b) {
                switch (order) {
                    case "characterID":
                        return a.characterID.localeCompare(b.characterId);
                    case "characterName":
                        let characterA = "";
                        let characterB = "";
                        for (let c of characters) {
                            if (c.characterName === a.characterName) {
                                characterA = c.characterName;
                            }
                            if (c.characterId === b.characterId) {
                                characterB = c.characterName;
                            }
                        }
                        return characterA.localeCompare(characterB);
                    case "class":
                        if (a.class !== b.class) {
                            return a.class.localeCompare(b.class);
                        } else {
                            return a.class.localeCompare(b.class);
                        }
                }
            });

            let characterHTML = `<div class="container">`;

            for (let character of data.characters) {

                let characterHTML = `<div class=\"container\">`;

                characterHTML += `<div class="row mb-2 border-bottom">`

                    + `<div class="col p-2 align-bottom">`
                    + `<div class="font-weight-bold">${character.characterID}</div>`
                    + `<div class="font-weight-bold">${character.characterName})</div>`
                    + `<div class="font-italic text-muted">${character.class}</div>`
                    + `</div>`

                    + `<div class="col-xl text-right py-2">`
                    + `<a class="btn btn-sm btn-info m-1" style="width:100px;" href="/client/software.html?id=${character.id}">Character ID</a>`
                    + `<a class="btn btn-sm btn-info m-1" style="width:100px;"  href="/client/accessories.html?id=${character.id}">Name</a>`
                    + `<a class="btn btn-sm btn-success m-1" style="width:75px;" href="/client/editsystem.html?id=${character.id}">Level</a>`
                    + `</div>`

                    + `</div>`;

            }
            characterHTML += `</div>`;

            document.getElementById('characters').innerHTML = characterHTML;
        }

    })

}