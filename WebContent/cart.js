let cart = $("#cart");

/**
 * Handle the data returned by IndexServlet
 * @param resultDataString jsonObject, consists of session info
 */
function handleSessionData(resultDataString) {

    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle session response");
    console.log(resultDataJson);

    // show cart information
    handleCartArray(resultDataJson["previousItems"]);
}

/**
 * Handle the items in item list
 * @param resultArray jsonObject, needs to be parsed to html
 */
function handleCartArray(resultArray) {
    console.log(resultArray);
    let item_list = $("#item_list");

    for (let i = 0; i < resultArray.length; i++) {
        let res = "";
        res += "<tr>";
        res += "<th>"
            + '<a href="single-movie.html?id=' + resultArray[i]["movie_id"] + '">'
            + resultArray[i]["movie_title"] + '</a>' + "</th>";
        res += "<th>"
            + resultArray[i]["quantity"]
            + "</th>";
        res += "<th> $"
            + resultArray[i]["price"]
            + "</th>";
        res += "<th> $"
            + resultArray[i]["price"] * resultArray[i]["quantity"]
            + "</th>";
        res += "</tr>";

        item_list.append(res);
    }    // change it to html list
}

/**
 * Submit form content with POST method
 * @param cartEvent
 */
function handleCartInfo(cartEvent) {
    console.log("submit cart form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    cartEvent.preventDefault();
    console.log(cart); // Check the value of cart


    $.ajax("api/cart", {
        method: "POST",
        data: cart.serialize(),
        success: resultDataString => {
            let resultDataJson = JSON.parse(resultDataString);
            handleCartArray(resultDataJson["previousItems"]);
        }
    });

    // clear input form
    // Clear input form sif cart is defined
    if (cart) {
        cart[0].reset();
    }
}

$.ajax("api/cart", {
    method: "GET",
    success: handleSessionData
});

// Bind the submit action of the form to a event handler function
cart.submit(handleCartInfo);
// Event delegation for dynamically injected form
// $(document).ready(function() {
//     // Event delegation for dynamically injected form
//     $(document).on('submit', '#cart', handleCartInfo);
// });


