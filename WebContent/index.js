/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */


/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleGenreResult(resultData) {
    console.log("handleGenreResult: populating genre from resultData");

    const charArray = ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C',
        'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
        'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '*']

    let alphabetListElement = jQuery("#alphabetical_list")

    for(let j = 0; j < charArray.length; j++) {
        let alphText = "";

        alphText += '<div class="grid-item"><a href="movie-list.html?alphabet_id=';

        if (charArray[j] === '*') {
            alphText += 'none';
        } else {
            alphText += charArray[j]
        }
        alphText += '">' + charArray[j] + '</a></div>';

        alphabetListElement.append(alphText);
    }
    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let genreListTableBodyElement = jQuery("#genre_list_table_body");

    // Iterate through resultData, no more than 20 entries
    for (let i = 0; i <  resultData.length; i++) {

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML +=
            "<th>" +
            // Add a link to single-movie.html with id passed with GET url parameter
            '<a href="movie-list.html?genre_id=' + resultData[i]['genre_id'] + '">'
            + resultData[i]["genre_name"] +     // display star_name for the link text
            '</a>' +
            "</th>";
        //rowHTML += "<th>" + resultData[i]["star_names"] + "</th>";
        rowHTML += "</tr>";

        // console.log(rowHTML);

        // Append the row created to the table body, which will refresh the page
        genreListTableBodyElement.append(rowHTML);
    }
}

function submitSearch() {
    let movieTitle = document.getElementById("movie_title").value.toString();
    let movieYear = document.getElementById("movie_year").value.toString();
    let movieDirector = document.getElementById("movie_director").value.toString();
    let movieStar = document.getElementById("movie_star").value.toString();

    let url = "movie-list.html?search=true"

    if (movieTitle !== "") {
        url += "&movie_title=" + movieTitle;
    }
    if (movieYear !== "")
        url += "&movie_year=" + movieYear;

    if (movieDirector !== "")
        url += "&movie_director=" + movieDirector;

    if (movieStar !== "")
        url += "&movie_star=" + movieStar;

    window.location.href = url;
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/mainpage", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleGenreResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});