function efectivo_tab(){
    $("#title_efectivo").addClass("active").attr("aria-selected",true);
    $("#content_efectivo").addClass("active show");

    $("#title_online").removeClass("active").removeAttr("aria-selected",true);
    $("#content_online").removeClass("active show");
    $("#title_pos").removeClass("active").removeClass("aria-selected",true);
    $("#content_pos").removeClass("active show");
}

function online_tab(){
    $("#title_online").addClass("active").attr("aria-selected",true);
    $("#content_online").addClass("active show");

    $("#title_pos").removeClass("active").removeClass("aria-selected",true);
    $("#content_pos").removeClass("active show");
    $("#title_efectivo").removeClass("active").removeClass("aria-selected",true);
    $("#content_efectivo").removeClass("active show");
}

function pos_tab(){
    $("#title_pos").addClass("active").attr("aria-selected",true);
    $("#content_pos").addClass("active show");

    $("#title_online").removeClass("active").removeAttr("aria-selected",true);
    $("#content_online").removeClass("active show");
    $("#title_efectivo").removeClass("active").removeClass("aria-selected",true);
    $("#content_efectivo").removeClass("active show");
}

