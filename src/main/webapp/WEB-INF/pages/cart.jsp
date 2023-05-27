<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="cart" type="com.es.phoneshop.model.cart.Cart" scope="session"/>

<tags:master pageTitle="Cart">
    <c:if test="${not empty param.message && empty errors}">
        <div class="success">
            ${param.message}
        </div>
    </c:if>
    <c:if test="${not empty param.checkoutError}">
        <div class="error">
            ${param.checkoutError}
        </div>
    </c:if>
    <c:if test="${not empty errors}">
        <div class="error">
            An error occurred during the update
        </div>
    </c:if>
    <p>
        Total quantity: ${cart.totalQuantity}
    </p>
    <c:set var="contextPath" value="${pageContext.servletContext.contextPath}"/>
    <form method="post">
        <table>
            <thead>
            <tr>
                <td>Image</td>
                <td>
                    Description
                </td>
                <td class="quantity">
                    Quantity
                </td>
                <td class="price">
                    Price
                </td>
            </tr>
            </thead>
            <c:forEach var="item" items="${cart.items}">
                <c:set var="product" value="${item.product}"/>
                <tr>
                    <td>
                        <img class="product-tile" src="${product.imageUrl}">
                    </td>
                    <td>
                        <a href="${contextPath}/products/${product.id}">
                            ${product.description}
                        </a>
                    </td>
                    <td class="quantity">
                        <c:set var="error" value="${errors[product.id]}"/>
                        <input name="quantity" value="${not empty error ? paramValues['quantity'][status.index] : item.quantity}" class="quantity" type="number"  min="1" pattern="[0-9]+"/>
                        <c:if test="${not empty errors[product.id]}">
                            <div class="error">
                                ${errors[product.id]}
                            </div>
                        </c:if>
                        <input type="hidden" name="productId" value="${product.id}"/>
                    </td>
                    <td class="price">
                        <a href="${contextPath}/priceHistory/${product.id}"/>
                        <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="${product.currency.symbol}"/>
                        </a>
                    </td>
                    <td>
                        <button form="deleteCartItem"
                                formaction="${contextPath}/cart/deleteCartItem/${product.id}">
                            Delete
                        </button>
                    </td>
                </tr>
            </c:forEach>
            <tr>
                <td></td>
                <td></td>
                <td>Total cost:</td>
                <td class="price">
                    <fmt:formatNumber value="${cart.totalCost}" type="currency" currencySymbol="${product.currency.symbol}"/>
                </td>
            </tr>
        </table>
        <p>
            <button>Update</button>
        </p>
    </form>
    <form action="${contextPath}/checkout">
            <button>Checkout</button>
    </form>
    <form id="deleteCartItem" method="post">
    </form>
</tags:master>