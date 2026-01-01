//package com.zunza.ecommerce.domain.category
//
//import io.kotest.assertions.throwables.shouldThrow
//import io.kotest.matchers.shouldBe
//import org.junit.jupiter.api.Test
//
//class CategoryTest {
//    @Test
//    fun createCategory() {
//        val category = CategoryFixture.createCategory()
//
//        category.name shouldBe "상의"
//        category.parentId shouldBe null
//        category.depth shouldBe 1
//        category.displayOrder shouldBe 1
//        category.isActive shouldBe true
//    }
//
//    @Test
//    fun createCategoryFail() {
//        shouldThrow<IllegalArgumentException> {
//            CategoryFixture.createCategory(parentId = 2)
//        }.message shouldBe "최상위 카테고리는 부모 카테고리를 가질 수 없습니다."
//
//        shouldThrow<IllegalArgumentException> {
//            CategoryFixture.createCategory(name = "")
//        }.message shouldBe "카테고리 이름은 필수입니다."
//
//        shouldThrow<IllegalArgumentException> {
//            CategoryFixture.createCategory(depth = 4)
//        }.message shouldBe "카테고리 계층은 최대 3단계까지 가능합니다."
//
//        shouldThrow<IllegalArgumentException> {
//            CategoryFixture.createCategory(displayOrder = 0)
//        }.message shouldBe "카테고리 표시 순서는 1 이상이어야 합니다."
//    }
//}