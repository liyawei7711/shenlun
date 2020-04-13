/**
 * officejs.js
 * Ver. : 1.0.1 
 * last update: 15/11/2017
 * Author: meshesha , https://github.com/meshesha
 * LICENSE: MIT
 * url:https://meshesha.github.io/officeJS
 */

(function ( $ ) {
   
    /////////////////////////////////////////////////////
    $.fn.officejs = function( options ) {
        var $result = $(this);
        var divId = $result.attr("id");
		 var defaults = {
            // These are the defaults.
            url: "",
            inputObjId: "",
            sheetSetting: {
                jqueryui: false,
                activeHeaderClassName: "",
                allowEmpty: true,
                autoColumnSize: true,
                autoRowSize: false,
                columns: false,
                columnSorting: true,
                contextMenu: false,
                copyable: true,
                customBorders: false,
                fixedColumnsLeft: 0,
                fixedRowsTop: 0,
                language: "en-US",
                search: false,
                selectionMode: "single",
                sortIndicator: false,
                readOnly: false,
                startRows: 1,
                startCols: 1,
                rowHeaders: true,
                colHeaders: true,
                width: false,
                height: false
            }
        };
        var settings = $.extend(true, {}, defaults, options);
        //1.get file memeType
        //2.load all js file needed to read the file
        var file = settings.url;
        var inputId = settings.inputObjId;
        var fileObj = null;
        if(file != ""){
            fileObj = {
                Obj : file, 
                ext : file.split('.').pop().toLowerCase()
            }
            getContent(fileObj,divId,settings.sheetSetting);
        }
        if(inputId != ""){
            //TODO
            $("#"+inputId).on("change", function(e) {
                //var inputFileObj = $(this)[0].files[0];
                var inputFileObj = e.target.files[0];
                if(inputFileObj !== undefined){
                    var fName = inputFileObj.name;
                    fileBlob = URL.createObjectURL(inputFileObj);
                    fileObj = {
                        Obj : fileBlob,
                        ext : fName.split('.').pop().toLowerCase()
                    }
                }
                getContent(fileObj,divId,settings.sheetSetting);
            });
        }
    }
    function getContent(fObj,divId,sheetSet){
        var ext = fObj.ext;
        var file = fObj.file;
        switch (ext) {
            case "pdf":
                //handel pdf file (https://mozilla.github.io/pdf.js/) -- V
                getPdfContent(fObj,divId);
                break;
            case "docx":
                //handel docx (https://github.com/mwilliamson/mammoth.js) -- V
                getDocxContent(fObj,divId);
                break;
            case "doc":
                //handel doc file () -- X
                getDocContent(fObj,divId);
                break;
            case "pptx":
                //handel pptx file (https://meshesha.github.io/pptxjs/) -- V
                getPptxContent(fObj,divId);
                break;
            case "ppt":
                //handel ppt file () -- X
                getPptContent(fObj,divId);
                break;
            case "xlsx":
            case "xls":
            case "xlw":
            case "xlsb":
            case "xlsm":
            case "csv":
            case "dbf":
            case "dif":
            case "slk":
            case "sylk":
            case "prn":
            case "ods":
            case "fods":
                //handel sheet file (https://github.com/sheetjs/js-xlsx) -- V
                getSheetContent(fObj,divId,sheetSet);
                break;
            case "gif":
            case "jpg":
            case "jpeg":
            case "bmp":
            case "tiff":
            case "tif":
            case "png":
            case "svg":
               //handel imge  -- V
               getImgContent(fObj,divId);
                break;
            default:
                unknownMsg(divId);
        }
        return
    }
    /////////////////////////////////////////////////PDF//////////////////////////////////////////
    function getPdfContent(fObj,divId){
        $("#"+divId).html("");
        // The workerSrc property shall be specified.
        //PDFJS.workerSrc = './Content/pdfViewer/pdf.worker.js';
        var file = fObj.Obj;

        scale = 2;
         $("#resolt_content").css({"margin-left":"360px"})
        //var options = options || { scale: 1 };
        
        function renderPage(page) {
			
            var viewport = page.getViewport(scale); //options.scale
		
            var canvas = document.createElement('canvas');
			
            var ctx = canvas.getContext('2d');
			
            var renderContext = {
            canvasContext: ctx,
            viewport: viewport
            };
            
            canvas.height = viewport.height;
            canvas.width = viewport.width;
            $("#"+divId).append(canvas);
            
            page.render(renderContext);
        }
        
        function renderPages(pdfDoc) {
            for(var num = 1; num <= pdfDoc.numPages; num++)
                pdfDoc.getPage(num).then(renderPage);
        }
        //PDFJS.disableWorker = true;
        PDFJS.getDocument(file).then(renderPages);

        return
    }
    /////////////////////////////////////////////////Docx//////////////////////////////////////////
    function getDocxContent(fObj,divId){
    
        $("#"+divId).html("");
		$("#"+divId).prepend(
		$("<span></span>").attr({
			"class":"slides-loadnig-msg",
			"style":"display:block; color:blue; font-size:20px; width:10%; margin:0 auto;"
		}).html("Loading...")
		);
        var file = fObj.Obj;
       
        JSZipUtils.getBinaryContent(file, function (err, content) {
            mammoth.convertToHtml({ arrayBuffer: content })
                .then(displayResult)
                .done();
        });
        
        function displayResult(result) {
            var rslt = result.value;
            var position = rslt.search(/[\u0590-\u05FF]/);
            if (position >= 0) {
                $('#'+divId).attr("dir", "rtl");
            } else {
                $('#'+divId).attr("dir", "ltr");
            }

            $('#'+divId).html(rslt);
            if (result.messages != "") {
                var messageHtml = result.messages.map(function (message) {
                    return message.type + ': ' + message.message + "\n";
                }).join("");

                console.log("Docx viewer Messages: \n" + messageHtml + "\n");
            }

        }
        return
    }
    /////////////////////////////////////////////////doc//////////////////////////////////////////
    function getDocContent(fObj,divId){
        
        $("#"+divId).html("");
        var file = fObj.Obj;

        var ran5 = 10000+Math.round(Math.floor()*90000);
        var subDiv = $('<div/>').attr({ class:'doc_files', id:"doc_file_"+ran5,style:"color:#9d9999;font-size:30pt"});
        $("#"+divId).append(subDiv);
        $("#doc_file_"+ran5).html(".doc file is not supported, convert it to .docx file");
        return
    }
    /////////////////////////////////////////////////pptx//////////////////////////////////////////
    function getPptxContent(fObj,divId){
         $("#"+divId).html("");
       
        //console.log(fObj,divId);
        var file = fObj.Obj;
		
         $("#resolt_content").css({"margin-left":"500px"})
        /*$("#"+divId).pptxToHtml({
            pptxFileUrl: file,
            slideMode: false,
            keyBoardShortCut: false,
            mediaProcess: true

        });*/
          
		$("#"+divId).pptxToHtml({
		
            pptxFileUrl: file,
            slideMode: false,
            keyBoardShortCut: false,
            mediaProcess: true,
		
			
        });
		
        return
    }
    /////////////////////////////////////////////////ppt//////////////////////////////////////////
    function getPptContent(fObj,divId){
        
        $("#"+divId).html("");
        var file = fObj.Obj;
        
        var ran5 = 10000+Math.round(Math.floor()*90000);
        var subDiv = $('<div/>').attr({ class:'ppt_files', id:"ppt_file_"+ran5,style:"color:#9d9999;font-size:30pt"});
        $("#"+divId).append(subDiv);
        $("#ppt_file_"+ran5).html(".ppt file is not supported, convert it to .pptx file");
        
        return
    }
    /////////////////////////////////////////////////Sheet//////////////////////////////////////////
    function getSheetContent(fObj,divId,sheetSet){
		$("#"+divId).prepend(
		$("<span></span>").attr({
			"class":"slides-loadnig-msg",
			"style":"display:block; color:blue; font-size:20px; width:10%; margin:0px auto;"
		}).html("Loading...")
		);
        try{
			if (jQuery.ui === undefined && sheetSet.jqueryui) {
            console.log("You set jqueryui as true , but you not included jquery-ui.js and jquery-ui.css");
            sheetSet.jqueryui = false;
        }
        if (jQuery.ui && sheetSet.jqueryui) {
            $("." + divId + "_clas").tabs("destroy");
        }

        var file = fObj.Obj;
		
    $("#resolt_content").css({"margin-left":"0px","margin-top":"0px"})

        var $container, availableWidth, availableHeight;

        var hot, hot_ary = [];
      
        $container = $("#" + divId);
        $container.addClass(divId + "_clas");
        $container.addClass("wbSheets_clas");
 
        if (sheetSet.search) {
            $("<div></div>", {
                id: "wbSheets_search_warpper"
            }).appendTo('#' + divId);
            $("<input/>", {
                id: "wbSheets_search_field",
                type: "text",
                placeholder: "Search...",
                style: "border-radius: 5px; width:20%;"
            }).appendTo('#' + divId + " #wbSheets_search_warpper");

            $("<hr>", {
            }).appendTo('#' + divId);
        }
        //function process_wb(wb) {
        //}
        var url = file;

        var req = new XMLHttpRequest();
        req.open("GET", url, true);
        req.responseType = "arraybuffer";
        req.onload = function (e) {
			$("#"+divId).html("");
            var data = new Uint8Array(req.response);
            var wb = XLSX.read(data, { type: "array" });
            var sheetNames = wb.SheetNames;
			
            $container.append('<ul class="wbSheets_clas_ul">');
            var li_container = "";
            sheetNames.forEach(function (sheetName, idx) {
                var subDivId = 'wbSheets_' + idx;
                var slcted = "";
                if (!sheetSet.jqueryui && idx == 0) {
                    slcted = "selected"
                }
                $("#" + divId + " ul").append('<li class = "' + slcted + '"><a href="#' + subDivId + '">' + sheetName + '</a></li>');
            });
            sheetNames.forEach(function (sheetName, idx) {
                var subDivId = 'wbSheets_' + idx;
                var json = XLSX.utils.sheet_to_json(wb.Sheets[sheetName], { header: 1 });
                var dsply = "";
                if (!sheetSet.jqueryui) {
                    if (idx == 0) {
                        dsply = "display:block;";
                    } else {
                        dsply = "display:none;";
                    }
                }
                var subDiv = $('<div/>').attr({
                    class: 'wbSheets',
                    id: subDivId,
                    style: dsply
                });
                $container.append(subDiv);
                //availableWidth = Math.max(subDiv.width(),600);
                //availableHeight = Math.max(subDiv.height(), 500);
                availableWidth = $container.width() + 1110;
                availableHeight = $container.height() + ((sheetSet.search) ? 200 :100);
                /* add header row for table */
                if (!json) json = [];
                json.forEach(function (r) {
                    //must "...,{header:1}"
                    if (json[0].length < r.length) json[0].length = r.length;
                });
                //console.log(json)
                var container = document.getElementById(subDivId);
                hot = new Handsontable(container, {
                    data: json,
                    activeHeaderClassName: sheetSet.activeHeaderClassName,
                    allowEmpty: sheetSet.allowEmpty,
                    autoColumnSize: sheetSet.autoColumnSize,
                    autoRowSize: sheetSet.autoRowSize,
                    columns: sheetSet.columns,
                    columnSorting: sheetSet.columnSorting,
                    contextMenu: sheetSet.contextMenu,
                    copyable: sheetSet.copyable,
                    fixedColumnsLeft: sheetSet.fixedColumnsLeft,
                    fixedRowsTop: sheetSet.fixedRowsTop,
                    language: sheetSet.language,
                    search: sheetSet.search,
                    selectionMode: sheetSet.selectionMode,
                    sortIndicator: sheetSet.sortIndicator,
                    readOnly: sheetSet.readOnly,
                    startRows: sheetSet.startRows,
                    startCols: sheetSet.startCols,
                    rowHeaders: sheetSet.rowHeaders,
                    colHeaders: sheetSet.colHeaders,
                    width: (!sheetSet.width) ? availableWidth : sheetSet.width,
                    height: (!sheetSet.height) ? availableHeight : sheetSet.height
                });
                hot_ary.push(hot);
            });

            if (jQuery.ui && sheetSet.jqueryui) {
                $("." + divId + "_clas").tabs();
            } else {
                $("." + divId + "_clas li").click(function (e) {
                    e.preventDefault();
                    $("." + divId + "_clas li").removeClass("selected");
                    $(this).addClass("selected");
                    var hrf = $($(this)[0].firstChild).attr("href").substr(1);
                    $("." + divId + "_clas .wbSheets").hide();
                    $("." + divId + "_clas #" + hrf).show();
                });
            }
            if (sheetSet.search) {
                var searchFiled = document.getElementById('wbSheets_search_field');
                Handsontable.dom.addEvent(searchFiled, 'keyup', function (event) {
                    var srch_txt = this.value;
                    hot_ary.forEach(function (hot_val) {
                        var search = hot_val.getPlugin('search');
                        var queryResult = search.query(srch_txt);
                        hot_val.render();
                    })
                });

            }
            //$("."+divId+"_clas").tabs("refresh");
            /*
            availableWidth = $container.width()-105;
            availableHeight = $container.height()-70;
           $("."+divId+"_clas .wbSheets .ht_master .wtHolder").css({
                width: availableWidth,
                height:availableHeight
           });
           $("."+divId+"_clas .wbSheets .ht_master .wtHolder").on("scroll",function(){
               
                $("."+divId+"_clas .wbSheets .ht_master .wtHolder").css({
                    width: availableWidth,
                    height:availableHeight
                });
           })
           */
        };
        req.send();
  
        return 
		}catch(e){alert(e)}
    }
    /////////////////////////////////////////////////Images//////////////////////////////////////////
    function getImgContent(fObj,divId){
        
        $("#"+divId).html("");
        var file = fObj.Obj;
        
        var img_div = $("#"+divId);
        img_div.html("");
        var myImage = new Image();//Image(100, 200)
        myImage.src = file;
        img_div.append(myImage);
        return
    }
    /////////////////////////////////////////////////Unknown file//////////////////////////////////////////
    function unknownMsg(divId){
        
        $("#"+divId).html("");
        
        var ran5 = 10000+Math.round(Math.floor()*90000);
        var subDiv = $('<div/>').attr({ class:'unknown_files', id:"unknown_file_"+ran5,style:"color:#9d9999;font-size:30pt"});
        $("#"+divId).append(subDiv);
        $("#unknown_file_"+ran5).html("The file is not supported!");
        return
    }
}(jQuery));