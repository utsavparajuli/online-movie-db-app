let cart = $("#cart");
let item_list = $("#item_list");
let cart_2;
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

    console.log(cart);

    let totalCost = 0;

    // $(document).ready(function() {
        for (let i = 0; i < resultArray.length; i++) {

            let cartAddString = "  <form ACTION='#' id='cart_2' METHOD='post'>" +
                "    <input name='item' type='hidden' value='" + resultArray[i]["movie_id"] + "+'>" +
                "    <input name='type' type='hidden' value=\"add\">" +
                "    <input type='submit' VALUE=\"+\">" +
                "   </form>"

            let cartRemoveString = "  <form ACTION='#' id='cart_2' METHOD='post'>" +
                "    <input name='item' type='hidden' value='" + resultArray[i]["movie_id"] + "-'>" +
                "    <input name='type' type='hidden' value=\"remove\">" +
                "    <input type='submit' VALUE=\"-\">" +
                "   </form>"

            let price = (Number(resultArray[i]["price"] * resultArray[i]["quantity"])).toFixed(2);
            totalCost += (Number(price));

            let res = "";
            res += "<tr>";
            res += "<td>"
                + '<a href="single-movie.html?id=' + resultArray[i]["movie_id"] + '">'
                + resultArray[i]["movie_title"] + '</a>' + "</td>";
            res += "<td> <span class='quantity'>"
                + resultArray[i]["quantity"]
                + "</span></td>";
            res += "<td> $"
                + (Number(resultArray[i]["price"])).toFixed(2)
                + "</td>";
            res += "<td> $"
                + (Number(resultArray[i]["price"] * resultArray[i]["quantity"])).toFixed(2)
                + "</td>";
            res += "<td>" + cartAddString + cartRemoveString + "<br></td>";

            // res += "<td><button class='add'>+</button><button class='subtract'>-</button></td>";
            res += "</tr>";


            item_list.append(res);

        }    // change it to html list


        if (resultArray.length > 0) {
            item_list.append("<br><tr> <th></th><th>Total: </th><th>$" + totalCost + "</th><th></th></tr>");

            item_list.append("<br><br><tr> <th></th><th></th><th></th><th></th><a href=\"payment.html\">" +
                "<button id=\"payment_button\" value=\"Payment\">Proceed to Payment</button></a></tr>");
        }

    // Handle the submit event of the parent container (item_list) and delegate it to the "cart_2" form
        item_list.on("submit", "#cart_2", function(event) {
            // event.preventDefault(); // Prevent the default form submission
            let cartEvent = $(this); // Use the current form that triggered the submit event

            console.log(event.currentTarget)
            cart_2 = $(event.currentTarget)

            console.log(cart_2)
            // Handle the form submission for "cart_2"
            handleCartChange(event);
        });
        // Event delegation for dynamically added buttons
        // item_list.on("click", ".add", function () {
        //     let quantityElement = $(this).closest("tr").find(".quantity");
        //     let currentQuantity = parseInt(quantityElement.text());
        //     quantityElement.text(currentQuantity + 1);
        //     console.log(quantityElement)
        //
        //     console.log(cart);
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
        // });
        //
        // item_list.on("click", ".subtract", function () {
        //     let quantityElement = $(this).closest("tr").find(".quantity");
        //     let currentQuantity = parseInt(quantityElement.text());
        //     if (currentQuantity > 0) {
        //         quantityElement.text(currentQuantity - 1);
        //     }
        //
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
        //     console.log(quantityElement)
        //
        // });
    // });
}



/**
 * Submit form content with POST method
 * @param cartEvent
 */
function handleCartInfo(cartEvent) {
    console.log("submit cart form");
    console.log(cartEvent);
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


/**
 * Submit form content with POST method
 * @param cartEvent
 */
function handleCartChange(cartEvent) {
    console.log("cart change form");
    console.log(cartEvent);


    // cartEvent.preventDefault();
    console.log(cart_2); // Check the value of cart


    $.ajax("api/cart", {
        method: "POST",
        data: cart_2.serialize(),
        success: resultDataString => {
            let resultDataJson = JSON.parse(resultDataString);
            handleCartArray(resultDataJson["previousItems"]);
        }
    });

    // clear input form
    // Clear input form sif cart is defined
    if (cart_2) {
        cart_2[0].reset();
    }
}




$.ajax("api/cart", {
    method: "GET",
    success: handleSessionData
});

// Bind the submit action of the form to a event handler function
cart.submit(handleCartInfo);

// cart_2.submit(handleCartChange);


// Event delegation for dynamically injected form
// $(document).ready(function() {
//     // Event delegation for dynamically injected form
//     $(document).on('submit', '#cart', handleCartInfo);
// });


