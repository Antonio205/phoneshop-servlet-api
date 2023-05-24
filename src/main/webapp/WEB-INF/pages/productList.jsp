<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="products" type="java.util.ArrayList" scope="request"/>

<tags:master pageTitle="Product List">
  <p>
    Welcome to Expert-Soft training!
  </p>
  <p>
       Cart: ${cart}
  </p>

  <c:if test="${not empty param.message && empty errors}">
         <div class="success">
             ${param.message}
         </div>
  </c:if>
  <c:if test="${not empty errors}">
      <div class="error">
          An error occurred during adding product
      </div>
  </c:if>

  <form>
    <input name="query" value="${param.query}">
    <button>Search</button>
  </form>
  <c:set var="contextPath" value="${pageContext.servletContext.contextPath}"/>
  <table>
    <thead>
      <tr>
        <td>Image</td>
        <td>
         Description
         <tags:sortLink sort="description" order="asc"/>
         <tags:sortLink sort="description" order="desc"/>
        </td>
        <td class="quantity">
            Quantity
        </td>
        <td>
            Adding to cart
        </td>
        <td class="price">
         Price
           <tags:sortLink sort="price" order="asc"/>
           <tags:sortLink sort="price" order="desc"/>
        </td>
      </tr>
    </thead>
    <c:forEach var="product" items="${products}" varStatus="status">
      <tr>
        <td>
          <img class="product-tile" src="${product.imageUrl}">
        </td>
        <td>
            <a href="${contextPath}/products/${product.id}">
                ${product.description}
            </a>
        </td>
        <c:set var="error" value="${errors[product.id]}"/>
        <form action="${contextPath}/addCart/${product.id}" method="post">
          <input type="hidden" name="productId" value="${product.id}"/>
          <td class="quantity">
              <input name="quantity" value="${not empty error ? paramValues['quantity'][status.index] : 1}" class="quantity" type="number" min="1" pattern="[0-9]+"/>
              <c:if test="${not empty errors}">
                  <div class="error">
                      ${errors[product.id]}
                  </div>
              </c:if>
          </td>

          <td>
            <button type="submit">Add to cart</button>
          </td>
        </form>

        <td class="price">
            <a href="${contextPath}/priceHistory/${product.id}"/>
                 <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="${product.currency.symbol}"/>
            </a>
        </td>
      </tr>
    </c:forEach>
  </table>

  <%@ include file="/WEB-INF/pages/recentlyViewedProducts.jsp"%>
</tags:master>