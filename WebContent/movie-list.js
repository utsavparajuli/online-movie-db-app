/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param target
 */

function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    console.log(url.toString())
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    let idResponse = decodeURIComponent(results[2].replace(/\+/g, " "));
    console.log(target + idResponse);
    return idResponse;
    // return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function handleResult(resultData) {
    console.log("handleResult: populating star table from resultData");
    console.log(resultData);

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let movieListTableBodyElement = jQuery("#movie_list_table_body");

    for (let i = 0; i < Math.min(20, resultData.length); i++) {

        let rowHTML = "<tr>";
        rowHTML += "<th>" +
            // Add a link to single-movie.html with id passed with GET url parameter
            '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '">'
            + resultData[i]["movie_title"] + '</a>' + "</th>";

        rowHTML += "<th>" + resultData[i]["year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["director"] + "</th>";
        rowHTML += "<th>";

        // add all genres and links to their pages
        for (let j = 0; j < 3; j++) {
            if (resultData[i]["genres"][j] !== undefined) {
                rowHTML += '<a href="movie-list.html?genre_id=' + resultData[i]["genres"][j]["id"] +
                    '">' + resultData[i]["genres"][j]["name"] + '</a>';

                rowHTML += ", ";
            }
        }
        rowHTML = rowHTML.substring(0, rowHTML.length - 2);
        rowHTML += "</th><th>";

        // Add all stars and create links to their pages
        for (let j = 0; j < 3; j++) {
            if (resultData[i]["stars"][j] !== undefined) {
                rowHTML += '<a href="single-star.html?id=' + resultData[i]["stars"][j]["id"] +
                    '">' + resultData[i]["stars"][j]["name"] + '</a>';
                rowHTML += ", ";
            }
        }
        rowHTML = rowHTML.substring(0, rowHTML.length - 2);
        rowHTML += "</th>";
        rowHTML += "<th>" + resultData[i]["rating"] + "</th>" + "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieListTableBodyElement.append(rowHTML);
    }
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Look for all possible modifications to the url\
let urlAddon = "";
let urlGenreId = getParameterByName('genre_id');
let alphabetId = getParameterByName('alphabet_id');

// Use to create the urlAddon to link to the appropriate webpage
if (urlGenreId != null) {
    urlAddon = "genre_id=" + urlGenreId;
} else {
    urlAddon = "alphabet_id=" + alphabetId;
}

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/movie-list?" + urlAddon, // Setting request url
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data
});