package android.buiducha.translateapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "vocabulary")
data class Vocabulary(
    @PrimaryKey
    var id: UUID,
    val lang: String,
    val text: List<String>,
) {
    var sourceText = String()
}