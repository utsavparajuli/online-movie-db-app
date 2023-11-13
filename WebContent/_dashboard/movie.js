let submit_movie_form = $('#submit_movie_form')


function handleMovieSubmit(orderEvent) {
    console.log("submitting movie form");
    orderEvent.preventDefault();
    let submitData = $('#submit_movie_form').serializeArray();

    $.ajax("api/movie", {
            method: "POST",
            data: submitData,
            success: (resultData) => handleInsertResult(resultData)
        }
    );
}

function handleInsertResult(resultData) {
    let resultDataJson = JSON.parse(resultData);
    console.log(resultDataJson);

    if (resultDataJson["update"] === "none") {
        $("#movie_submit_message").text("Movie Already Exists :(");
    } else {
        let submitString = "Movie Added! Movie ID: " +
            resultDataJson["movieId"] + " Star ID: " +
            resultDataJson["starId"] + " Genre ID: " +
            resultDataJson["genreId"];
        $("#movie_submit_message").text(submitString);
    }
}

submit_movie_form.submit(handleMovieSubmit);
