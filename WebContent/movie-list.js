/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleStarResult(resultData) {
    console.log("handleStarResult: populating star table from resultData");

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
        rowHTML += "<th>" + resultData[i]["genres"] + "</th>";

        rowHTML += "<th>";
        let count = 0;
        for (let j = 0; j < 3; j++) {
            if (resultData[i]["stars"][j] !== undefined) {
                rowHTML += '<a href="single-star.html?id=' + resultData[i]["stars"][j]["id"] +
                    '">' + resultData[i]["stars"][j]["name"] + '</a>';
                if (j < 2) {
                    rowHTML += ", ";
                }
            }
        }
        rowHTML += "</th>"
        rowHTML += "<th>" + resultData[i]["rating"] + "</th>" + "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieListTableBodyElement.append(rowHTML);
    }
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/movielist", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});