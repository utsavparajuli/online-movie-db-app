/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param target
 */

function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    //console.log(url.toString())
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    //console.log(target + "=" + idResponse);
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function handleResult(resultData) {
    console.log("handleResult: populating movieList table from resultData");
    console.log(resultData);

    $("#movie_list_table_body").empty();
    let movieListTableBodyElement = jQuery("#movie_list_table_body");

    let numMoviesRequested = getParameterByName("num_results");
    console.log("requested number:" + numMoviesRequested);
    if (numMoviesRequested != null) {
        let numMovies = parseInt(numMoviesRequested);
        if (resultData.length < numMovies) {
            console.log("less than " + numMovies.toString() + " results, disable next button");
            document.getElementById("next_page").className = "page-item disabled";
        }
    } else {
        if (resultData.length < 25) {
            console.log("less than 25 results, disable next button");
            document.getElementById("next_page").className = "page-item disabled";
        }
    }

    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = "<tr><th>" +
            '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '">'
            + resultData[i]["movie_title"] + "</a></th>";
        rowHTML += "<th>" + resultData[i]["year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["director"] + "</th>";
        rowHTML += "<th>";
        for (let j = 0; j < 3; j++) {
            if (resultData[i]["genres"][j] !== undefined) {
                rowHTML += '<a href="movie-list.html?genre_id=' + resultData[i]["genres"][j]["id"] +
                    '">' + resultData[i]["genres"][j]["name"] + '</a>';
                rowHTML += ", ";
            }
        }
        rowHTML = rowHTML.substring(0, rowHTML.length - 2);
        rowHTML += "</th><th>";
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
        movieListTableBodyElement.append(rowHTML);
    }

    let currentUrl = window.location.href.split("?")[1];
    let urlFirstParam = currentUrl.split("&")[0];
    console.log("current url: " + currentUrl);
    console.log("url Main Part: " + urlFirstParam);
    let newUrl = "";
    if (currentUrl.length > 20) {
        newUrl = currentUrl;
    } else {
        newUrl = "movie-list.html?" + urlFirstParam + "&num_results=" + document.getElementById("num_results").value;
        let currentPageNumber = parseInt(document.getElementById("current_page").innerText);
        newUrl += "&offset=" + (currentPageNumber - 1).toString();
        let sortArray = document.getElementById("sort_order").value.split(" ");
        newUrl += "&first_sort=" + sortArray[0] +
            "&first_dir=" + sortArray[1] +
            "&second_sort=" + sortArray[2] +
            "&second_dir=" + sortArray[3] +
            "&back=true";
    }

    console.log("movieList back url: " + newUrl);
    sessionStorage.setItem("backButtonUrl", newUrl);

}

function generateUrl() {
    let currentUrl = window.location.href.split("?")[1];
    console.log("current url: " + currentUrl);
    const currentPageNumber = parseInt(document.getElementById("current_page").innerText);
    let newUrl = currentUrl + "&num_results=" + document.getElementById("num_results").value;
    newUrl += "&offset=" + currentPageNumber.toString();
    let sortArray = document.getElementById("sort_order").value.split(" ");
    let test = "&first_sort=" + sortArray[0] +
        "&first_dir=" + sortArray[1] +
        "&second_sort=" + sortArray[2] +
        "&second_dir=" + sortArray[3] +
        "&back=true";
    newUrl += test;
    console.log("next url " + newUrl);
    return newUrl;
}

function changePage(pageDirection) {
    const currentPageNumber = parseInt(document.getElementById("current_page").innerText);
    if (pageDirection < 0) {
        $("#current_page").text(currentPageNumber - 2);
    }
    let newUrl = generateUrl();
    if (pageDirection > 0)
        $("#current_page").text(currentPageNumber + 1);
    else
        $("#current_page").text(currentPageNumber - 1);

    console.log("direction " + pageDirection);

    if (pageDirection === 1) {
        document.getElementById("previous_page").className = "page-item";
    } else {
        if (currentPageNumber - 1 === 1) {
            document.getElementById("previous_page").className = "page-item disabled";
        }
        document.getElementById("next_page").className = "page-item";
    }
    window.scrollTo({top: 0, behavior: "smooth"});
    sendHttpRequest(newUrl);
}

/*
function goToNextPage() {
    let currentPageNumber = parseInt(document.getElementById("current_page").innerText);
    $("#current_page").text(currentPageNumber + 1);
    document.getElementById("previous_page").className = "page-item";
    let newUrl = generateUrl();
    window.scrollTo({top: 0, behavior: "smooth"});
    sendHttpRequest(newUrl);
}

function goToPreviousPage() {
    let currentPageNumber = parseInt(document.getElementById("current_page").innerText);
    $("#current_page").text(currentPageNumber - 1);
    if (currentPageNumber - 1 === 1) {
        document.getElementById("previous_page").className = "page-item disabled";
    }
    document.getElementById("next_page").className = "page-item";
    let currentUrl = window.location.href.split("?")[1];
    console.log("current url: " + currentUrl);
    let newUrl = currentUrl + "&num_results=" + document.getElementById("num_results").value;
    newUrl += "&offset=" + (currentPageNumber - 2).toString();
    let sortArray = document.getElementById("sort_order").value.split(" ");
    newUrl += "&first_sort=" + sortArray[0] +
        "&first_dir=" + sortArray[1] +
        "&second_sort=" + sortArray[2] +
        "&second_dir=" + sortArray[3] +
        "&back=true";
    console.log("new url: " + newUrl);
    window.scrollTo({top: 0, behavior: "smooth"});
    sendHttpRequest(newUrl);
}
*/

function submitSort() {
    let currentUrl = window.location.href.split("?")[1];
    console.log("current url: " + currentUrl);
    let newUrl = currentUrl + "&num_results=" + document.getElementById("num_results").value;
    let sortArray = document.getElementById("sort_order").value.split(" ");
    newUrl += "&first_sort=" + sortArray[0] +
    "&first_dir=" + sortArray[1] +
    "&second_sort=" + sortArray[2] +
    "&second_dir=" + sortArray[3];
    console.log("new url: " + newUrl);
    $("#current_page").text(1);
    document.getElementById("previous_page").className = "page-item disabled";
    sendHttpRequest(newUrl);
}

function createUrl() {
    let movieUrlAddon = "";

    if (getParameterByName('genre_id') != null) {
        movieUrlAddon = "genre_id=" + getParameterByName('genre_id');
    } else if (getParameterByName('alphabet_id') != null) {
        movieUrlAddon = "alphabet_id=" + getParameterByName('alphabet_id');
    } else {
        let movieTitle = getParameterByName('movie_title')
        if (movieTitle != null)
            movieUrlAddon += "&movie_title=" + movieTitle;

        let movieYear = getParameterByName('movie_year')
        if (movieYear != null)
            movieUrlAddon += "&movie_year=" + movieYear;

        let movieDirector = getParameterByName('movie_director')
        if (movieDirector != null)
            movieUrlAddon += "&movie_director=" + movieDirector;

        let movieStar = getParameterByName('movie_star');
        if (movieStar != null)
            movieUrlAddon += "&movie_star=" + movieStar;
    }
    console.log("createUrl = " + movieUrlAddon);
    return movieUrlAddon;
}

function sendHttpRequest(url) {
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/movie-list?" + url, // Setting request url
        success: (resultData) => handleResult(resultData) // Setting callback function to handle data
    });
}

function reloadPageInformation(url) {
    let offset = getParameterByName(url, "offset");
    $("#current_page").text(offset + 1);
    if (offset + 1 === 1) {
        document.getElementById("previous_page").className = "page-item disabled";
        document.getElementById("next_page").className = "page-item"
    } else {
        document.getElementById("previous_page").className = "page-item";
    }
}

console.log("1");

if (getParameterByName("back") != null) {
    console.log("Checking for saved page");
    if (sessionStorage.getItem("backButtonUrl") != null) {
        let url = sessionStorage.getItem("backButtonUrl")
        url = url.split("?")[1];
        url = url.substring(0, url.length - 10);
        console.log("SUCCESS: " + url);
        sendHttpRequest(url);
    } else {
        console.log("no saved page, return to index")
        window.location.replace("index.html");
    }
} else {
    console.log("Going to createUrl()");
    sendHttpRequest(createUrl());
}