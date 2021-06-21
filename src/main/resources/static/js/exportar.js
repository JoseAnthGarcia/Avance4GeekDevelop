const $btnExportar = document.querySelector("#btnExportar"),
    $tabla = document.querySelector("#tabla");

$btnExportar.addEventListener("click", function() {


    let nombre_pagina = $("#nombre_pagina").text();
    let nombreFormat = formatName(nombre_pagina);
    let date = formatDate();
    console.log(nombreFormat);
    console.log(date);
    let xlsx_name = date + "_"+ nombreFormat ;

    console.log(xlsx_name);

    let tableExport = new TableExport($tabla, {
        exportButtons: false, // No queremos botones
        filename: xlsx_name, //Nombre del archivo de Excel
        sheetname: "Reporte ", //Título de la hoja
    });
    let datos = tableExport.getExportData();
    let preferenciasDocumento = datos.tabla.xlsx;
    tableExport.export2file(preferenciasDocumento.data, preferenciasDocumento.mimeType, preferenciasDocumento.filename, preferenciasDocumento.fileExtension, preferenciasDocumento.merges, preferenciasDocumento.RTL, preferenciasDocumento.sheetname);
});

function formatDate() {
    let now = new Date();
    return now.toLocaleDateString().replaceAll("/","_");
}

function formatName(name){
    return name.toLowerCase().replaceAll(" ","_");
}