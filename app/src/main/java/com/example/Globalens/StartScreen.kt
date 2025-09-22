import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun TopBarWithIcons() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Hamburger menu
        Row(modifier = Modifier.fillMaxWidth(0.142f).height(50.dp)
            .background(color = Color.LightGray, shape = RoundedCornerShape(30.dp)),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { /* Handle menu */ },
                modifier = Modifier
                    .background(Color.White, shape = CircleShape)
                    .size(40.dp)
            ) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
        }


        // Right-side icons
        Row(
            modifier = Modifier.fillMaxWidth(.3f).height(50.dp)
                .background(color = Color.LightGray, shape = RoundedCornerShape(30.dp)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly) {
            // Search Icon
            IconButton(
                onClick = { /* Handle search */ },
                modifier = Modifier
                    .background(Color.White, shape = CircleShape)
                    .size(40.dp)
            ) {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }
            IconButton(
                onClick = { /* Handle search */ },
                modifier = Modifier
                    .background(Color.White, shape = CircleShape)
                    .size(40.dp)
            ) {
                Icon(Icons.Default.Notifications, contentDescription = "Notifications")

            }
        }


    }
}
