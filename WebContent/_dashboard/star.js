let submit_star_form = $('#submit_star_form');
let starId = "";

function getNewStarId (resultData) {
    console.log(resultData);
    starId = resultData["id"];
}


function handleInsertResult(resultData) {
    console.log(resultData);
    if (resultData["check"] !== null) {
        $("#star_submit_message").text("Star inserted! ID: " + starId);
    } else {
        $("#star_submit_message").text("Star insert failed :(");
    }
}

function handleStarSubmit(orderEvent) {
    console.log("submitting star form");
    orderEvent.preventDefault();
    let submitData = $("#submit_star_form").serializeArray();
    submitData.push({name: "star_id", value: starId});
    $.ajax("api/star", {
        method: "POST",
        data: submitData,
        success: (resultData) => handleInsertResult(resultData)
        }
    );
}

$.ajax("api/star", {
    dataType: "json",
    method: "GET",
    success: (resultData) => getNewStarId(resultData)
});

submit_star_form.submit(handleStarSubmit);