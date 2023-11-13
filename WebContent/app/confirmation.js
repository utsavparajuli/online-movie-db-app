$.ajax("api/confirmation", {
    method: "GET",
    success: handleOrders
});


function handleOrders(resultDataString) {

    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle order confirmation response");
    console.log(resultDataJson);

    let total_confirmation = $("#total_confirmation");

    total_confirmation.append("<h3 class='text-center'> $" + resultDataJson["total"] + "</h3>");

    let salesJson = resultDataJson["sales"];
    let ordersBody = $("#orders_body");

    // $(document).ready(function() {
    for (let i = 0; i < salesJson.length; i++) {
        let res = "";
        res += "<tr>";
        res += "<td>" + salesJson[i]["movie_name"] + "</td>";
        res += "<td> <span class='quantity'>"
            + salesJson[i]["quantity"]
            + "</span></td>";
        res += "<td>" + salesJson[i]["sale_ids"] +  "</td>";

        res += "</tr>";


        ordersBody.append(res);
    }

}