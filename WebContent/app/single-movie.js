/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs three steps:
 *      1. Get parameter from request URL, so it know which id to look for
 *      2. Use jQuery to talk to backend API to get the json data.
 *      3. Populate the data to correct html elements.
 */


/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
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
    console.log(idResponse);
    return idResponse;
    // return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {

    console.log("handleResult: populating movie info from resultData");
    console.log(resultData);

    var count = Object.keys(resultData).length;
    // console.log(count);
    // console.log(resultData);

    // populate the star info h3
    // find the empty h3 body by id "star_info"
    let movieInfoElement = jQuery("#movie_info");

    // append two html <p> created to the h3 body, which will refresh the page
    movieInfoElement.append("<div align='center'><h1><b>" + resultData[0]["title"] + "</b></h1><i> (" +
        resultData[0]["year"] + ")</i><br><br>");

    let moviePurchaseElement = jQuery("#movie_purchase");

    moviePurchaseElement.append('<input name="item" type="hidden" value="' + resultData[0]["movie_id"] + '+">');
    // moviePurchaseElement.append('<input name="item_title" type="hidden" value="' + resultData[0]["movie_title"] + '">');


    let movieSubInfoElement = jQuery("#movie_sub_info");

    let sub_info = "<tr>";
    sub_info += "<th>" + resultData[0]["director"] + "</th>";
    sub_info += "<th>" + resultData[0]["rating"] + "</th>";

    sub_info += "<th>";
    // add all genres and links to their pages
    let genreNum = parseInt(resultData[0]["genre_num"]);
    for (let i = 0; i < genreNum; i++) {
        if (resultData[0]["genres"][i] !== undefined) {
            sub_info += '<a href="movie-list.html?genre_id=' + resultData[0]["genres"][i]["id"] +
                '">' + resultData[0]["genres"][i]["name"] + '</a>';

            sub_info += ", ";
        }
    }
    sub_info = sub_info.substring(0, sub_info.length - 2);
    sub_info += "</th>";


    movieSubInfoElement.append(sub_info)
    console.log("handleResult: populating movie table from resultData - updated");

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let starTableBodyElement = jQuery("#star_table_body");

    let starNum = parseInt(resultData[0]["star_num"])
    for (let i = 0; i < starNum; i++) {
        let sub_info = "";
        sub_info += "<tr>";
        sub_info += "<th>"
            + '<a href="single-star.html?id=' + resultData[0]["stars"][i]["id"] + '">'
            + resultData[0]["stars"][i]["name"] + '</a>' + "</th>";
        sub_info += "</tr>";

        // Append the row created to the table body, which will refresh the page
        starTableBodyElement.append(sub_info);
    }
}


function goBackToSessionPage() {
    if (sessionStorage.getItem("backToHome") !== null) {
        sessionStorage.removeItem("backToHome");
        window.location.href = "index.html"
    } else if (sessionStorage.getItem("prevPage") !== null) {
        sessionStorage.removeItem("prevPage");
        window.location.href = "top-20-rated.html";
    } else {
        sessionStorage.setItem("backCheck", "yes");
        window.location.href = "movie-list.html?" + sessionStorage.getItem("baseUrl");
    }
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let movieId = getParameterByName('id');
console.log("check2: " + sessionStorage.getItem("prevPage"));

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});