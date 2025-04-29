package com.example.MobileApp.model


data class Card(
    val id: Int,
    val platform: String,
    val card_number: String,
    val card_type: String,
    val bank: String,
    val balance: Double,
    val cashback: Double,
    val commission: Double
)

data class SourceOption(
    val card_number: String, val label: String
)

data class LoginResponse(
    val token: String
)

data class TransferRequest(
    val source_card_number: String, val destination_card_number: String, val amount: Double
)

data class TransferResponse(
    val source: Card, val destination: Card
)