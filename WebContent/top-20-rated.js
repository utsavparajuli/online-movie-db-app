/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleStarResult(resultData) {
    console.log("handleStarResult: populating star table from resultData");
    console.log(resultData);

    let movieListTableBodyElement = jQuery("#movie_list_table_body");

    for (let i = 0; i < Math.min(20, resultData.length); i++) {
        let rowHTML = "<tr>";
        rowHTML += "<th>" +
            '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '">'
            + resultData[i]["movie_title"] + "</a></th>";

        rowHTML += "<th>" + resultData[i]["year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["director"] + "</th><th>";
        for (let j = 0; j < 3; j++) {
            if (resultData[i]["genres"][j] !== undefined) {
                rowHTML += '<a href="movie-list.html?genre_id=' + resultData[i]["genres"][j]["id"] +
                    '">' + resultData[i]["genres"][j]["name"] + '</a>';
                rowHTML += ", ";
            }
        }
        rowHTML = rowHTML.substring(0, rowHTML.length - 2);
        rowHTML += "</th><th>";
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
        movieListTableBodyElement.append(rowHTML);
    }
}

function removeStoredSessionInfo() {
    sessionStorage.removeItem("num_results");
    sessionStorage.removeItem("offset");
    sessionStorage.removeItem("sortedCheck");
    sessionStorage.removeItem("backCheck");
    sessionStorage.removeItem("baseUrl");
    sessionStorage.removeItem("first_sort");
    sessionStorage.removeItem("first_dir");
    sessionStorage.removeItem("second_sort");
    sessionStorage.removeItem("second_dir");
}

removeStoredSessionInfo();
sessionStorage.setItem("prevPage", "top20");
console.log("check: " + sessionStorage.getItem("prevPage"))

/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/top20rated", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});