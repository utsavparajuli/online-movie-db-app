/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param target
 */
let cart_list;
let movieListTableBodyElement = jQuery("#movie_list_table_body");


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

function addToCartFromList(movieId) {
    console.log("added to cart from movie id");
    console.log(movieId);
}

function handleResult(resultData) {
    console.log("handleResult: populating movieList table from resultData");
    console.log(resultData);

    // let movieListTableBodyElement = jQuery("#movie_list_table_body");
    $("#movie_list_table_body").empty();

    //TODO TESTING RIGHT HERE
    reloadPaginationInformation(resultData.length);

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
        let noStars = false;
        let starsNotAvailable = true;
        rowHTML = rowHTML.substring(0, rowHTML.length - 2);
        rowHTML += "</th><th>";
        for (let j = 0; j < 3; j++) {
            if (resultData[i]["stars"][j] !== undefined) {
                rowHTML += '<a href="single-star.html?id=' + resultData[i]["stars"][j]["id"] +
                    '">' + resultData[i]["stars"][j]["name"] + '</a>';
                rowHTML += ", ";
                starsNotAvailable = false;
            }
            else {
                noStars = true;
            }
        }

        if(noStars && starsNotAvailable) {
            rowHTML += "N/A  ";
        }
        rowHTML = rowHTML.substring(0, rowHTML.length - 2);
        rowHTML += "</th>";
        rowHTML += "<th>" + resultData[i]["rating"] + "</th>";
        let movieIdForCart = resultData[i]["movie_id"];

        //TODO: the item is not being added onto the cart through the movie-list
        rowHTML += "<th>" + '<form ACTION="#" id="cart_list" METHOD="POST">' +
            '    <label> <input name="item" type="hidden" value="' + movieIdForCart + '+"></label>' +
            '<input name="type" type="hidden" value="add">' +
            '    <input type="submit"  class="btn btn-default" VALUE="add to cart">' +
            '  </form> </th> </tr>';

        // Append the row created to the table body, which will refresh the page
        movieListTableBodyElement.append(rowHTML);

    }
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

function generateUrl(pageDirection) {
    //let currentUrl = window.location.href.split("?")[1];
    let currentUrl = sessionStorage.getItem("baseUrl");
    console.log("current base url: " + currentUrl);

    let newUrl = "";
    const currentPageNumber = parseInt(document.getElementById("current_page").innerText);
    console.log("currentPageNumber: " + currentPageNumber);

    if (sessionStorage.getItem("backCheck") === "yes" || sessionStorage.getItem("sortedCheck") === "yes") {
        let offset = parseInt(sessionStorage.getItem("offset"));
        console.log("offset: " + offset);
        console.log("new offset: " + offset + ", page direction: " + pageDirection + " = " + (offset + pageDirection));
        newUrl = currentUrl +
            "&num_results=" + sessionStorage.getItem("num_results") +
            "&offset=" + (parseInt(sessionStorage.getItem("offset")) + pageDirection) +
            "&first_sort=" + sessionStorage.getItem("first_sort") +
            "&first_dir=" + sessionStorage.getItem("first_dir") +
            "&second_sort=" + sessionStorage.getItem("second_sort") +
            "&second_dir=" + sessionStorage.getItem("second_dir");
        sessionStorage.setItem("offset", (offset + pageDirection));
        console.log("\n\nURL CREATED FROM SORTED PAGE OR BACK PAGE: " + newUrl + "\n\n");
        //sessionStorage.removeItem("sortedCheck");

    } else {
        newUrl = currentUrl + "&num_results=" + document.getElementById("num_results").value;
        newUrl += "&offset=" + currentPageNumber.toString();
        let sortArray = document.getElementById("sort_order").value.split(" ");
        newUrl += "&first_sort=" + sortArray[0] +
            "&first_dir=" + sortArray[1] +
            "&second_sort=" + sortArray[2] +
            "&second_dir=" + sortArray[3];
        sessionStorage.setItem("num_results", document.getElementById("num_results").value);
        sessionStorage.setItem("offset", currentPageNumber.toString());
        sessionStorage.setItem("first_sort", sortArray[0]);
        sessionStorage.setItem("first_dir", sortArray[1]);
        sessionStorage.setItem("second_sort", sortArray[2]);
        sessionStorage.setItem("second_dir", sortArray[3]);
        sessionStorage.removeItem("sortedCheck");
        sessionStorage.removeItem("backCheck");
        console.log("\n\nURL CREATED FROM HTML: " + newUrl + "\n\n");
    }

    //sessionStorage.setItem("backButtonUrl", newUrl);
    return newUrl;
}

function changePage(pageDirection) {
    const currentPageNumber = parseInt(document.getElementById("current_page").innerText);
    if (pageDirection < 0) {
        $("#current_page").text(currentPageNumber - 2);
        console.log("dir -1: " + (currentPageNumber - 2));
    }
    let newUrl = generateUrl(pageDirection);
    //if (getParameterByName("back") ==  null)
    $("#current_page").text(currentPageNumber + pageDirection);
    console.log("next page number: " + (currentPageNumber + pageDirection));

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

function submitSort() {
    //let currentUrl = window.location.href.split("?")[1];
    let currentUrl = sessionStorage.getItem("baseUrl");
    console.log("current url (sort): " + currentUrl);
    let newUrl = currentUrl + "&num_results=" + document.getElementById("num_results").value;
    let sortArray = document.getElementById("sort_order").value.split(" ");
    newUrl += "&first_sort=" + sortArray[0] +
    "&first_dir=" + sortArray[1] +
    "&second_sort=" + sortArray[2] +
    "&second_dir=" + sortArray[3];
    console.log("new url: " + newUrl);
    $("#current_page").text(1);
    document.getElementById("previous_page").className = "page-item disabled";

    sessionStorage.setItem("first_sort", sortArray[0]);
    sessionStorage.setItem("first_dir", sortArray[1]);
    sessionStorage.setItem("second_sort", sortArray[2]);
    sessionStorage.setItem("second_dir", sortArray[3]);
    sessionStorage.setItem("num_results", document.getElementById("num_results").value);
    sessionStorage.setItem("offset", "0");
    sessionStorage.setItem("sortedCheck", "yes");

    console.log("PAGE SORTED, SESSION STORED");

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
    updateSessionStorage(movieUrlAddon);
    return movieUrlAddon;
}

function updateSessionStorage(movieUrlAddon) {
    console.log("createUrl = " + movieUrlAddon);
    sessionStorage.setItem("baseUrl", movieUrlAddon);
    sessionStorage.setItem("num_results", "25");
    sessionStorage.setItem("offset", "0");
    sessionStorage.setItem("first_sort", "rating");
    sessionStorage.setItem("first_dir", "DESC");
    sessionStorage.setItem("second_sort", "title");
    sessionStorage.setItem("second_dir", "ASC");
    sessionStorage.removeItem("backCheck");
    sessionStorage.removeItem("sortedCheck");
}

function sendHttpRequest(url) {
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/movie-list?" + url, // Setting request url
        success: (resultData) => handleResult(resultData) // Setting callback function to handle data
    });
}

function reloadPaginationInformation(resultDataLength) {
    let numMoviesRequested = parseInt(sessionStorage.getItem("num_results"))
    console.log("requested number: " + numMoviesRequested);
    if (numMoviesRequested != null) {
        if (resultDataLength < numMoviesRequested) {
            console.log("less than " + numMoviesRequested + " results, disable next button");
            document.getElementById("next_page").className = "page-item disabled";
        } else if (numMoviesRequested === resultDataLength) {
            document.getElementById("next_page").className = "page-item";
        }

        if (sessionStorage.getItem("backCheck") === "yes") {
            let offset = parseInt(sessionStorage.getItem("offset"));
            console.log("offset: " + offset);
            let newPageNumber = offset + 1;
            console.log("Page Number: " + newPageNumber);
            $("#current_page").text(newPageNumber);
            if (newPageNumber === 1) {
                document.getElementById("previous_page").className = "page-item disabled";
                console.log("prev page disabled");
            } else {
                document.getElementById("previous_page").className = "page-item";
                console.log("prev page enabled");
            }
        }


    } else {
        if (resultDataLength < 25) {
            console.log("less than 25 results, disable next button");
            document.getElementById("next_page").className = "page-item disabled";
        }
    }
}


sessionStorage.removeItem("prevPage");

if (sessionStorage.getItem("backCheck") === "yes") {
    console.log("backPage was set");
    let url = sessionStorage.getItem("baseUrl") +
        "&num_results=" + sessionStorage.getItem("num_results") +
        "&offset=" + sessionStorage.getItem("offset") +
        "&first_sort=" + sessionStorage.getItem("first_sort") +
        "&first_dir=" + sessionStorage.getItem("first_dir") +
        "&second_sort=" + sessionStorage.getItem("second_sort") +
        "&second_dir=" + sessionStorage.getItem("second_dir");
    console.log("URL AFTER COMING BACK FROM A SINGLE PAGE: " + url);
    sessionStorage.removeItem("backCheck");
    sendHttpRequest(url);
} else {
    console.log("Going to createUrl()");
    sendHttpRequest(createUrl());
}

movieListTableBodyElement.on("submit", "#cart_list", function(event) {
    // event.preventDefault(); // Prevent the default form submission
    let cartEvent = $(this); // Use the current form that triggered the submit event

    console.log(event.currentTarget)
    cart_list = $(event.currentTarget)

    console.log(cart_list);

    cart = cart_list;
    // Handle the form submission for "cart_2"
    handleCartInfo(event);
});
