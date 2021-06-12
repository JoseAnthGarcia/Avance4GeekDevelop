
function escogerMetodoPago(){

    if($('#online').is(':checked') || $('#pos').is(':checked') || $('#efectivo').is(':checked')){
        if($('#online').is(':checked')){
            $("#title_online").addClass("active").attr("aria-selected",true);
            $("#content_online").addClass("active show");

            $("#pos").attr("disabled",true);
            $("#efectivo").attr("disabled",true);
        }else if($('#pos').is(':checked')){
            $("#title_pos").addClass("active").attr("aria-selected",true);
            $("#content_pos").addClass("active show");

            $("#online").attr("disabled",true);
            $("#efectivo").attr("disabled",true);
        }else if($('#efectivo').is(':checked')){
            $("#title_efectivo").addClass("active").attr("aria-selected",true);
            $("#content_efectivo").addClass("active show");

            $("#pos").attr("disabled",true);
            $("#online").attr("disabled",true);
        }
    }else{
        $("#title_online").removeClass("active").removeAttr("aria-selected",true);
        $("#content_online").removeClass("active show");
        $("#title_pos").removeClass("active").removeAttr("aria-selected",true);
        $("#content_pos").removeClass("active show");
        $("#title_efectivo").removeClass("active").removeAttr("aria-selected",true);
        $("#content_efectivo").removeClass("active show");

        $("#online").attr("disabled",false);
        $("#pos").attr("disabled",false);
        $("#efectivo").attr("disabled",false);
    }
}
