<#macro navbar>
<nav class="navbar navbar-expand-lg navbar-light bg-light">
    <div class="container-fluid">
        <!--        <a class="navbar-brand" href="#">Navbar</a>-->
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse"
                data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent"
                aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarSupportedContent">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item">
                    <a class="nav-link active" aria-current="page" href="/">Home</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="/cart/">Cart</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="/admin/">Admin</a>
                </li>
                <#if loggedin??>
                <li class="nav-item">
                    <form action="/logout" method="post"><button type="submit" class="btn btn-light">Logout</button></form>
                </li>
                <#else>
                <li class="nav-item">
                    <form action="/login"><button type="submit" class="btn btn-light">Login</button></form>
                </li>
                </#if>
            </ul>
            <form class="d-flex" action="/products">
                <input class="form-control me-2" type="search" name="search" placeholder="Search"
                       aria-label="Search"/>
                <button class="btn btn-outline-success" type="submit">Search</button>
            </form>
        </div>
    </div>
</nav>
</#macro>