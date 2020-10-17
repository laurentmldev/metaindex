<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  

<!DOCTYPE html>
<html lang="en">

<head>

  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
  <meta name="description" content="">
  <meta name="author" content="">

  <title>Metaindex Error</title>

  <!-- Custom fonts for this template-->
  <link href="${webAppBaseUrl}/public/commons/deps/fontawesome-free/css/all.min.css" rel="stylesheet" type="text/css">
  <link href="https://fonts.googleapis.com/css?family=Nunito:200,200i,300,300i,400,400i,600,600i,700,700i,800,800i,900,900i" rel="stylesheet">

  <!-- Custom styles for this template-->
  <link href="${webAppBaseUrl}/public/commons/style/css/sb-admin-2.css" rel="stylesheet">

</head>

<body id="page-top">

  <!-- Page Wrapper -->
  <div id="wrapper" style="height:100vh;">
    

    <!-- Content Wrapper -->
    <div id="content-wrapper" class="d-flex flex-column" style="background-image:url('${webAppBaseUrl}/public/commons/media/img/bluescreen-of-death.jpg');">

      <!-- Main Content -->
      <div id="content" style="background-color:#fff;opacity:0.95;">

        <!-- Begin Page Content -->
        <div class="container-fluid">

          <!-- Process Error Text -->
          <div class="text-center" style="padding-top:20vh">
            <div class="error mx-auto" data-text="ERROR">ERROR</div>
            <p class="lead text-gray-800 mb-5">Woops something wrong happened or maybe you don't have access to such contents, sorry!</p>
            <p class="text-gray-600 mb-0" style="padding-bottom:5rem">If you think this is a serious problem, 
            							please try to understand how to reproduce it <br/>and explain it to your system administrator, 
            							so that he can investigate and fix this issue.</p>
            <a  href="${webAppBaseUrl}/welcome">&larr; Go Back to Life</a>
          </div>

        </div>
        <!-- /.container-fluid -->

      </div>
      <!-- End of Main Content -->

      <!-- Footer -->
      <footer class="sticky-footer bg-white">
        <div class="container my-auto">
         
        </div>
      </footer>
      <!-- End of Footer -->

    </div>
    <!-- End of Content Wrapper -->

  </div>
  <!-- End of Page Wrapper -->


  <!-- Bootstrap core JavaScript-->
  <script src="${webAppBaseUrl}/public/commons/deps/jquery/jquery.min.js"></script>
  <script src="${webAppBaseUrl}/public/commons/deps/bootstrap/js/bootstrap.bundle.min.js"></script>

  <!-- Core plugin JavaScript-->
  <script src="${webAppBaseUrl}/public/commons/deps/jquery-easing/jquery.easing.min.js"></script>

  <!-- Custom scripts for all pages-->
  <script src="${webAppBaseUrl}/public/commons/js/sb-admin-2.min.js"></script>

</body>

</html>
