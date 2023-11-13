let payment_form = $("#payment_form");


/**
 * Handle the data returned by IndexServlet
 * @param resultDataString jsonObject, consists of session info
 */
function handlePayment(resultDataString) {

    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle session response");
    console.log(resultDataJson);

    let total_cost = $("#total_cost");

    total_cost.append('<label>' + (Number (resultDataJson["total"])).toFixed(2) + '</label>')

    // show cart information
    console.log(resultDataJson["previousItems"]);
}

/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleOrderPlaced(resultDataString) {
    console.log(resultDataString)
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle order submit response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);
    console.log(resultDataJson["recorded"])

    // If login succeeds, it will redirect the user to index.html
    if (resultDataJson["status"] === "success") {
        window.location.replace("confirmation.html?sales=" + resultDataJson["recorded"]);
    } else {
        // If login fails, the web page will display
        // error messages on <div> with id "login_error_message"
        console.log("show error message");
        console.log(resultDataJson["message"]);
        $("#payment_error_message").text(resultDataJson["message"]);
    }
}


/**
 * Submit form content with POST method
 * @param orderEvent
 */
function handlePaymentSubmit(orderEvent) {
    console.log("submit payment form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    orderEvent.preventDefault();
    console.log(payment_form); // Check the value of cart


    $.ajax("api/payment", {
        method: "POST",
        data: payment_form.serialize(),
        success: handleOrderPlaced
        }
    );

    // clear input form
    // Clear input form sif cart is defined
    // if (payment_form) {
    //     payment_form[0].reset();
    // }
}

$.ajax("api/payment", {
    method: "GET",
    success: handlePayment
});

// Bind the submit action of the form to a event handler function
payment_form.submit(handlePaymentSubmit);


