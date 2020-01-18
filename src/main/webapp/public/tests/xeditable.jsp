<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<c:url value="/" var="mxurl"/>

<!DOCTYPE html>
<html><head>
  <meta http-equiv="content-type" content="text/html; charset=UTF-8">
  <title>xeditable bootstrap4 + fontawesome4 + adhoc css</title>
  <meta http-equiv="content-type" content="text/html; charset=UTF-8">
  <meta name="robots" content="noindex, nofollow">
  <meta name="googlebot" content="noindex, nofollow">
  <meta name="viewport" content="width=device-width, initial-scale=1">


      <link rel="stylesheet" type="text/css" href="${mxurl}public/commons/deps/bootstrap/scss/bootstrap.css">
      <script type="text/javascript" src="${mxurl}public/commons/deps/jquery/jquery.js"></script>
      <script type="text/javascript" src="${mxurl}public/commons/deps/bootstrap/js/bootstrap.js"></script>
      <link rel="stylesheet" type="text/css" href="${mxurl}public/commons/deps/bootstrap-editable/css/bootstrap-editable.css">
      <script type="text/javascript" src="${mxurl}public/commons/deps/bootstrap-editable/js/bootstrap-editable.js"></script>
      <link rel="stylesheet" type="text/css" href="${mxurl}public/commons/deps/fontawesome-free/css/fontawesome.css">

  <style id="compiled-css" type="text/css">
      .glyphicon-ok::before {
		  content: "\f00c";
		}
		
		.glyphicon-remove::before {
		  content: "\f00d";
		}
		
		.glyphicon {
		  font-family: 'FontAwesome';
		  font-style: normal;
		}

  </style>


  <!-- TODO: Missing CoffeeScript 2 -->

  <script type="text/javascript">//<![CDATA[

    window.onload=function(){
      
$(document).ready(function() {
  //toggle `popup` / `inline` mode
  $.fn.editable.defaults.mode = 'inline';

  //make username editable
  $('#username').editable();

  //make status editable
  $('#status').editable({
    type: 'select',
    title: 'Select status',
    placement: 'right',
    value: 2,
    source: [{
      value: 1,
      text: 'status 1'
    },
             {
               value: 2,
               text: 'status 2'
             },
             {
               value: 3,
               text: 'status 3'
             }
            ]
    /*
            //uncomment these lines to send data on server
            ,pk: 1
            ,url: '/post'
            */
  });
});


    }

  //]]></script>

</head>
<body>
    <div class="container">

  <h1>X-editable starter template</h1>

  <div>
    <span>Username:</span>
    <a href="#" id="username" data-type="text" data-placement="right" data-title="Enter username" class="editable editable-click">superuser</a>
  </div>

  <div>
    <span>Status:</span>
    <a href="#" id="status" class="editable editable-click">status 2</a>
  </div>
  
  <table id="user" class="table table-bordered table-striped" style="clear: both">
                <tbody> 
                    <tr>         
                        <td width="35%">Simple text field</td>
                        <td width="65%"><a href="#" id="username" data-type="text" data-pk="1" data-title="Enter username">superuser</a></td>
                    </tr>
                    <tr>         
                        <td>Empty text field, required</td>
                        <td><a href="#" id="firstname" data-type="text" data-pk="1" data-placement="right" data-placeholder="Required" data-title="Enter your firstname"></a></td>
                    </tr>  
                    <tr>         
                        <td>Select, local array, custom display</td>
                        <td><a href="#" id="sex" data-type="select" data-pk="1" data-value="" data-title="Select sex"></a></td>
                    </tr>
                    <tr>         
                        <td>Select, remote array, no buttons</td>
                        <td><a href="#" id="group" data-type="select" data-pk="1" data-value="5" data-source="/groups" data-title="Select group">Admin</a></td>
                    </tr> 
                    <tr>         
                        <td>Select, error while loading</td>
                        <td><a href="#" id="status" data-type="select" data-pk="1" data-value="0" data-source="/status" data-title="Select status">Active</a></td>
                    </tr>  
                         
                    <tr>         
                        <td>Datepicker</td>
                        <td>
                        
                        <span class="notready">not implemented for Bootstrap 3 yet</span>
                        
                        </td>
                    </tr>
                    <tr>         
                        <td>Combodate (date)</td>
                        <td><a href="#" id="dob" data-type="combodate" data-value="1984-05-15" data-format="YYYY-MM-DD" data-viewformat="DD/MM/YYYY" data-template="D / MMM / YYYY" data-pk="1"  data-title="Select Date of birth"></a></td>
                    </tr> 
                    <tr>         
                        <td>Combodate (datetime)</td>
                        <td><a href="#" id="event" data-type="combodate" data-template="D MMM YYYY  HH:mm" data-format="YYYY-MM-DD HH:mm" data-viewformat="MMM D, YYYY, HH:mm" data-pk="1"  data-title="Setup event date and time"></a></td>
                    </tr> 
                    
                                         
                                        
                    <tr>         
                        <td>Textarea, buttons below. Submit by <i>ctrl+enter</i></td>
                        <td><a href="#" id="comments" data-type="textarea" data-pk="1" data-placeholder="Your comments here..." data-title="Enter comments">awesome
user!</a></td>
                    </tr> 
                    
                    
                    
                    
                    <tr>         
                        <td>Twitter typeahead.js</td>
                        <td><a href="#" id="state2" data-type="typeaheadjs" data-pk="1" data-placement="right" data-title="Start typing State.."></a></td>
                    </tr>                       
                                         
                                                        
                    <tr>         
                        <td>Checklist</td>
                        <td><a href="#" id="fruits" data-type="checklist" data-value="2,3" data-title="Select fruits"></a></td>
                    </tr>

                    <tr>         
                        <td>Select2 (tags mode)</td>
                        <td><a href="#" id="tags" data-type="select2" data-pk="1" data-title="Enter tags">html, javascript</a></td>
                    </tr>                    

                    <tr>         
                        <td>Select2 (dropdown mode)</td>
                        <td><a href="#" id="country" data-type="select2" data-pk="1" data-value="BS" data-title="Select country"></a></td>
                    </tr>  
                    
                    <tr>         
                        <td>Custom input, several fields</td>
                        <td><a href="#" id="address" data-type="address" data-pk="1" data-title="Please, fill address"></a></td>
                    </tr>                      
                                                                                                                
                    <tr>         
                        <td>Wysihtml5 (now support bootstrap 3 !!!). <a href="#" id="pencil"><i class="icon-pencil" style="padding-right: 5px"></i>[edit]</a></td>
                        <td>
                          <div id="note" data-pk="1" data-type="wysihtml5" data-toggle="manual" data-title="Enter notes" data-placement="top">
                            <h3>WYSIWYG</h3>
                            WYSIWYG means <i>What You See Is What You Get</i>.<br>
                            But may also refer to:
                              <ul>
                                <li>WYSIWYG (album), a 2000 album by Chumbawamba</li>
                                <li>"Whatcha See is Whatcha Get", a 1971 song by The Dramatics</li>
                                <li>WYSIWYG Film Festival, an annual Christian film festival</li>
                              </ul>
                              <i>Source:</i> <a href="http://en.wikipedia.org/wiki/WYSIWYG_%28disambiguation%29">wikipedia.org</a> 
                            
                            </div>
                        </td>
                    </tr>
                </tbody>
            </table>


</div>


  
  <script>
    // tell the embed parent frame the height of the content
    if (window.parent && window.parent.parent){
      window.parent.parent.postMessage(["resultsFrame", {
        height: document.body.getBoundingClientRect().height,
        slug: "h5qmord2"
      }], "*")
    }

    // always overwrite window.name, in case users try to set it manually
    window.name = "result"
  </script>


</body></html>
