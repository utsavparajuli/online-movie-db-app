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

// /**
//  * Submit form content with POST method
//  * @param cartEvent
//  */
// function addToCart(cartEvent) {
//     console.log("submit cart form from movie-list");
//     console.log(cartEvent);
//     /**
//      * When users click the submit button, the browser will not direct
//      * users to the url defined in HTML form. Instead, it will call this
//      * event handler when the event is triggered.
//      */
//     cartEvent.preventDefault();
//     console.log(cart); // Check the value of cart
//
//
//     $.ajax("api/cart", {
//         method: "POST",
//         data: cart.serialize(),
//         success: resultDataString => {
//             let resultDataJson = JSON.parse(resultDataString);
//             handleCartArray(resultDataJson["previousItems"]);
//         }
//     });
//
//     // clear input form
//     // Clear input form sif cart is defined
//     if (cart) {
//         cart[0].reset();
//     }
// }

function handleResult(resultData) {
    console.log("handleResult: populating movieList table from resultData");
    console.log(resultData);

    // let movieListTableBodyElement = jQuery("#movie_list_table_body");
    $("#movie_list_table_body").empty();

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

    console.log("sessions url: " + sessionStorage.getItem("backButtonUrl"));

    let currentUrl = window.location.href.split("?")[1];
    let urlFirstParam = currentUrl.split("&")[0];

    console.log("current url(handleResult): " + currentUrl);
    console.log("url Main Part: " + urlFirstParam);

    let newUrl = "";
    // idea: check for presence of back rather than the
    // length of the url which will vary (based on # of searches or genre etc
    //if (getParameterByName("back") == null)
    if (currentUrl.length > 15) {
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
  
    // movieListTableBodyElement.on("submit", "#cart_list", function(event) {
    //     // event.preventDefault(); // Prevent the default form submission
    //     let cartEvent = $(this); // Use the current form that triggered the submit event
    //
    //     console.log(event.currentTarget)
    //     cart_list = $(event.currentTarget)
    //
    //     console.log(cart_list);
    //
    //     cart = cart_list;
    //     // Handle the form submission for "cart_2"
    //     handleCartInfo(event);
    // });
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

function generateUrl(pageDirection) {
    let currentUrl = window.location.href.split("?")[1];
    console.log("current url(generateUrl): " + currentUrl);

    let newUrl = "";
    const currentPageNumber = parseInt(document.getElementById("current_page").innerText);
    console.log("currentPageNumber: " + currentPageNumber);
    let testForBack = getParameterByName("back");

    if (testForBack != null) {
        console.log("\n\nBACK SUCCESS\n\n");
        let urlFirstParam = currentUrl.split("&")[0];
        newUrl = urlFirstParam +
            "&num_results=" + getParameterByName("num_results") +
            "&offset=" + (currentPageNumber + pageDirection - 1) +
            "&first_sort=" + getParameterByName("first_sort") +
            "&first_dir=" + getParameterByName("first_dir") +
            "&second_sort=" + getParameterByName("second_sort") +
            "&second_dir=" + getParameterByName("second_dir") +
            "&back=true";
    } else {
        newUrl = currentUrl + "&num_results=" + document.getElementById("num_results").value;
        newUrl += "&offset=" + currentPageNumber.toString();
        let sortArray = document.getElementById("sort_order").value.split(" ");
        let test = "&first_sort=" + sortArray[0] +
            "&first_dir=" + sortArray[1] +
            "&second_sort=" + sortArray[2] +
            "&second_dir=" + sortArray[3] +
            "&back=true";
        newUrl += test;
    }
    console.log("backButtonUrl: " + newUrl);
    sessionStorage.setItem("backButtonUrl", newUrl);
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
    //window.scrollTo({top: 0, behavior: "smooth"});
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

function reloadPaginationInformation(resultDataLength) {
    let numMoviesRequested = getParameterByName("num_results");
    console.log("requested number:" + numMoviesRequested);
    if (numMoviesRequested != null) {
        let numMovies = parseInt(numMoviesRequested);
        if (resultDataLength < numMovies) {
            console.log("less than " + numMovies.toString() + " results, disable next button");
            document.getElementById("next_page").className = "page-item disabled";
        }

        let checkForBack = getParameterByName("back");
        console.log("backCheck passed");
        if (checkForBack != null) {
            let offset = parseInt(getParameterByName("offset"));
            console.log("url: " + window.location.href);
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


console.log("1");

if (getParameterByName("back") != null) {
    console.log("Checking for saved page");
    if (sessionStorage.getItem("backButtonUrl") != null) {
        let url = sessionStorage.getItem("backButtonUrl")
        url = url.split("?")[1];
        url += "&back=true";
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
