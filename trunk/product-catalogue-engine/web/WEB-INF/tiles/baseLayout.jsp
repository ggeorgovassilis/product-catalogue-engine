<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
  <head>
    <title>PCE</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta name="title" content="PCE" />
    <meta name="description" content="Site description here" />
    <meta name="keywords" content="keywords here" />
    <meta name="language" content="en" />
    <meta name="subject" content="Site subject here" />
    <meta name="robots" content="All" />
    <meta name="copyright" content="Your company" />
    <meta name="abstract" content="Site description here" />
    <meta name="MSSmartTagsPreventParsing" content="true" />
    <link id="theme" rel="stylesheet" type="text/css" href="/product-catalogue-engine/static/css/screen.css" title="theme" />
  </head>
  <body> 
    <div id="wrapper"> 
      <div id="bg"> 
        <div id="header"><tiles:insertAttribute name="header" /></div>  
        <div id="page"> 
          <div id="container"> 
            <!-- banner -->  
            <div id="banner"></div>  
            <!-- end banner -->  
            <!--  content -->  
            <div id="content"> 
              <div id="left"> 
                <div id="sidebar"> 
                  <tiles:insertAttribute name="menu" />
                </div> 
              </div>  
              <div id="center"> 
                <div id="mainContent"> 
                  <tiles:insertAttribute name="body" />
                </div> 
              </div>  
              <div style="clear:both;height:40px"></div> 
            </div>  
            <!-- end content --> 
          </div>  
          <!-- end container --> 
        </div>  
        <div id="footerWrapper"> 
          <div id="footer"> 
            <tiles:insertAttribute name="footer" />
          </div> 
        </div> 
      </div> 
    </div> 
  </body>
</html>
