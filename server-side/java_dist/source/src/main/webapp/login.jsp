<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <title>Sign in &middot; Firerunner</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="description" content="">
        <meta name="author" content="">

        <!-- Le styles -->

        <style type="text/css">
            body {
                padding-top: 40px;
                padding-bottom: 40px;
                background-color: #f5f5f5;
            }

            .form-signin {
                max-width: 300px;
                padding: 19px 29px 29px;
                margin: 0 auto 20px;
                background-color: #fff;
                border: 1px solid #e5e5e5;
                -webkit-border-radius: 5px;
                -moz-border-radius: 5px;
                border-radius: 5px;
                -webkit-box-shadow: 0 1px 2px rgba(0,0,0,.05);
                -moz-box-shadow: 0 1px 2px rgba(0,0,0,.05);
                box-shadow: 0 1px 2px rgba(0,0,0,.05);
            }
            .form-signin .form-signin-heading,
            .form-signin .checkbox {
                margin-bottom: 10px;
            }
            .form-signin input[type="text"],
            .form-signin input[type="password"] {
                font-size: 16px;
                height: auto;
                margin-bottom: 15px;
                padding: 7px 9px;
            }

        </style>
        <link href="./css/bootstrap-responsive.css" rel="stylesheet">
        <link href="./css/bootstrap.css" rel="stylesheet">
        <script src="./js/jquery.js"></script>
        <script src="./js/bootstrap.js"></script>

        <!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
        <!--[if lt IE 9]>
          <script src="../assets/js/html5shiv.js"></script>
        <![endif]-->

        <!-- Fav and touch icons -->

    </head>

    <body>

        <div class="container">

            <%
                Object obj = request.getParameter("invalid");

            %>

            <form class="form-signin" action="sublogin.jsp" method="post">
                <h2 class="form-signin-heading">Please sign in</h2>
                <%
                    if (obj != null) {
                %>
                <input name="username" type="text" class="input-block-level error" placeholder="Email address">
                <input name="password" type="password" class="input-block-level error" placeholder="Password">
                <div class="alert alert-error">
                    <button type="button" class="close" data-dismiss="alert">&times;</button>
                    <h4>Opsss!</h4>
                    Invalid username
                </div>
                <%   
                    } else {
                %>
                <input name="username" type="text" class="input-block-level" placeholder="Email address">
                <input name="password" type="password" class="input-block-level" placeholder="Password">

                <%            
                    }
                %>
                <label class="checkbox">
                    <input type="checkbox" value="remember-me"> Remember me
                </label>
                <button class="btn btn-large btn-primary" type="submit" >Sign in</button>
            </form>

        </div> <!-- /container -->

        <!-- Le javascript
        ================================================== -->


    </body>
</html>
