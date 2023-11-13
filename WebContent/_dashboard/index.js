let submit_star_form = $("submit_star_form");
let submit_movie_form = $("submit_movie_form");

function handleTableMetadataResult(resultData) {
    console.log("handleTableMetadataResult: populating table metadata from resultData");
    console.log(resultData);
    let tableMetadataListElement = jQuery("#table_metadata_list_table_body");

    for (let i = 0; i < resultData.length; ++i) {
        let rowHTML = "<tr><th>" +resultData[i]["table_name"] + "</th>" +
            "<th>" + resultData[i]["column_name"] + "</th>" +
            "<th>" + resultData[i]["data_type"] + "</th>" + "</tr>";
        tableMetadataListElement.append(rowHTML);
    }
}

function handleStarSubmit() {
    console.log(submit_star_form)
}

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/index",
    success: (resultData) => handleTableMetadataResult(resultData)
});