<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:master pageTitle="Advanced search">
  <p>
    Welcome to Expert-Soft training!
  </p>

  <c:if test="${not empty param.message && empty error}">
    <div class="success">
      ${param.message}
    </div>
  </c:if>
  <c:if test="${not empty error}">
    <div class="error">
      An error occurred during the search
    </div>
  </c:if>

  <form method="get" action="${pageContext.request.contextPath}/advancedSearch">
    <label for="description">Description:</label>
    <input type="text" name="description" id="description" value="${param.description}">
    <br><br>

    <label for="wordType">Word Type:</label>
    <select name="wordType" id="wordType">
      <option value="ALL" ${param.wordType == 'ALL' ? 'selected' : ''}>All Words</option>
      <option value="ANY" ${param.wordType == 'ANY' ? 'selected' : ''}>Any Word</option>
    </select>
    <br><br>

    <label for="minPrice">Minimum Price:</label>
    <input type="text" name="minPrice" id="minPrice" value="${param.minPrice}" min="1" pattern="[0-9]+">
    <br><br>

    <label for="maxPrice">Maximum Price:</label>
    <input type="text" name="maxPrice" id="maxPrice" value="${param.maxPrice}" min="1" pattern="[0-9]+">
    <br><br>

    <button type="submit">Search</button>

    <c:if test="${not empty error}">
      <div class="error">
        ${error}
      </div>
    </c:if>
  </form>

  <c:set var="contextPath" value="${pageContext.servletContext.contextPath}" />
    <c:if test="${not empty products && not empty param}">
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
            <form action="${contextPath}/addToCart/${product.id}" method="post">
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
    </c:if>

  <%@ include file="/WEB-INF/pages/recentlyViewedProducts.jsp"%>
</tags:master>