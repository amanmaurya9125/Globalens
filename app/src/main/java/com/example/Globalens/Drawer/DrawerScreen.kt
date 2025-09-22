package com.example.Globalens.Drawer

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.Globalens.R
import com.example.Globalens.SharedPreferences
import com.example.Globalens.ViewModel.NewsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Drawer(navController: NavController, drawerState: DrawerState, viewModel: NewsViewModel) {
    var context = LocalContext.current
    var india by remember { mutableStateOf(false) }
    var sportState by remember { mutableStateOf(false) }
    var worldState by remember { mutableStateOf(false) }
    var techgadgets by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val fontSize: Int = 20
    val sharePref = SharedPreferences()
    val userPref = SharedPreferences().getUserAuth(context)
    var userauth = remember { mutableStateOf(false) }
    val userName = userPref["name"]
    val imageUrl = userPref["photo"]
    if (userPref["uid"] != null) {
        userauth.value = true
    }

    LazyColumn(
        modifier = Modifier
            .padding(15.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
                    .padding(vertical = 10.dp, horizontal = 15.dp)
            ) {
                if (userauth.value) {
                    drawerTop_After(userName.toString(), imageUrl.toString())
                } else {
                    drawerTop_Before(navController, drawerState)
                }
            }


            HorizontalDivider(modifier = Modifier.padding(vertical = 5.dp))
        }
        item {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(top = 5.dp, end = 5.dp, start = 5.dp, bottom = 5.dp)
                    .clickable {
                        navController.navigate("savedArticle") {
                            scope.launch {
                                drawerState.close()
                            }
                        }
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.unsaved),
                    contentDescription = "Unsaved",
                    modifier = Modifier.padding(horizontal = 5.dp)
                )
                Text(
                    "Saved Articles",
                    fontSize = fontSize.sp
                )

            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 5.dp))
        }
        item {

            // India
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(top = 5.dp, end = 5.dp, start = 5.dp, bottom = 5.dp)
                    .clickable {
                        india = !india
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "India",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.black_arrow),
                        contentDescription = "Black_Arrow",
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )

                }
                Row(
                    modifier = Modifier
                        .weight(1f),
                    horizontalArrangement = Arrangement.End
                ) {
                    Icon(
                        if (india) {
                            Icons.Default.KeyboardArrowUp
                        } else {
                            Icons.Default.KeyboardArrowDown
                        },
                        contentDescription = "Down Arrow"
                    )
                }
            }
            if (india) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(start = 8.dp, top = 4.dp, bottom = 10.dp)
                ) {
                    val state_List = listOf(
                        "Maharashtra",
                        "Delhi", "Karnataka", "Uttar Pradesh", "Tamilnadu", "Telangana",
                        "Andaman And Nicobar", "Andhra Pradesh", "Arunachal Pradesh", "Assam",
                        "Bihar", "Chandigarh", "Chhattisgarh", "Dadra & Nagar Haveli",
                        "Daman & Diu", "Goa"
                    )
                    state_List.forEach {
                        ChipItem(text = it, viewModel, navController)
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 5.dp))
        }
        item {
            //Entertainment
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(top = 5.dp, end = 5.dp, start = 5.dp, bottom = 5.dp)
                    .clickable {
                        viewModel.searchNews("Entertainment")
                        navController.navigate("searchResult")
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Entertainment",
                        fontSize = fontSize.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.black_arrow),
                        contentDescription = "Black_Arrow",
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )

                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 5.dp))
        }
        item {
            // Tech Gadgets
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(top = 5.dp, end = 5.dp, start = 5.dp, bottom = 5.dp)
                    .clickable {
                        techgadgets = !techgadgets
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(2f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Tech (Gadgets)",
                        fontSize = fontSize.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.black_arrow),
                        contentDescription = "arrow",
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )

                }
                Row(
                    modifier = Modifier
                        .weight(0.4f),
                    horizontalArrangement = Arrangement.End
                ) {
                    Icon(
                        if (techgadgets) {
                            Icons.Default.KeyboardArrowUp
                        } else {
                            Icons.Default.KeyboardArrowDown
                        },
                        contentDescription = "Down Arrow"
                    )
                }
            }
            if (techgadgets) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(start = 8.dp, top = 4.dp, bottom = 10.dp)
                ) {
                    val tech_List = listOf(
                        "Tech News",
                        "Gadgets", "AI News", "Gaming", "New Mobile", "Laptops",
                        "Computer", "Audio", "Tech Video", "Earbuds"
                    )
                    tech_List.forEach {
                        ChipItem(text = it, viewModel, navController)
                    }
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 5.dp))
        }
        item {
            //Real State
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(top = 5.dp, end = 5.dp, start = 5.dp, bottom = 5.dp)
                    .clickable {
                        viewModel.searchNews("Real State")
                        navController.navigate("searchResult")
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Real State",
                        fontSize = fontSize.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.black_arrow),
                        contentDescription = "Black_Arrow",
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )

                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 5.dp))
        }
        item {
            //Sports
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(top = 5.dp, end = 5.dp, start = 5.dp, bottom = 5.dp)
                    .clickable {
                        sportState = !sportState
                    },                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Sports",
                        fontSize = fontSize.sp,

                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.black_arrow),
                        contentDescription = "Black_Arrow",
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )

                }
                Row(
                    modifier = Modifier
                        .weight(1f),
                    horizontalArrangement = Arrangement.End
                ) {
                    Icon(
                        if (sportState) {
                            Icons.Default.KeyboardArrowUp
                        } else {
                            Icons.Default.KeyboardArrowDown
                        },
                        contentDescription = "Down Arrow"
                    )
                }
            }
            if (sportState) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(start = 8.dp, top = 4.dp, bottom = 10.dp)
                ) {
                    val sport_List = listOf(
                        "Cricket",
                        "WWE", "Football", "Tennis", "Wrestling", "Badminton",
                        "Golf",
                        "Boxing", "Cycling", "Volleyball", "Weightlifting"
                    )
                    sport_List.forEach {
                        ChipItem(text = it, viewModel, navController)
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 5.dp))
        }
        item {
            // World
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(top = 5.dp, end = 5.dp, start = 5.dp, bottom = 5.dp)
                    .clickable {
                        worldState = !worldState
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "World",
                        fontSize = fontSize.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.black_arrow),
                        contentDescription = "Black_Arrow",
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )

                }
                Row(
                    modifier = Modifier
                        .weight(1f),
                    horizontalArrangement = Arrangement.End
                ) {
                    Icon(
                        if (worldState) {
                            Icons.Default.KeyboardArrowUp
                        } else {
                            Icons.Default.KeyboardArrowDown
                        },
                        contentDescription = "Down Arrow"
                    )
                }
            }
            if (worldState) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(start = 8.dp, top = 4.dp, bottom = 10.dp)
                ) {
                    val world_List = listOf(
                        "China",
                        "Europe", "Middle East", "Pakistan", "South Asia", "UK",
                        "US",
                    )
                    world_List.forEach {
                        ChipItem(text = it, viewModel, navController)
                    }
                }
            }
            HorizontalDivider()

        }
        item {
//            Logout
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp, end = 5.dp, start = 5.dp, bottom = 20.dp)
                    .clickable {
                        if (userauth.value) {
                            viewModel.logout()
                            sharePref.clearUserAuth(context)
                            scope.launch {
                                drawerState.close()
                            }
                            Toast
                                .makeText(context, "Logout!!", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast
                                .makeText(context, "You're not Logged In", Toast.LENGTH_SHORT)
                                .show()
                        }
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Logout",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_logout_24),
                        contentDescription = "Black_Arrow",
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )

                }
            }

            HorizontalDivider()

        }
        item {
//            Spacer
            Spacer(modifier = Modifier.height(100.dp))

        }
    }
}

@Composable
fun ChipItem(text: String, viewModel: NewsViewModel, navController: NavController) {
    Box(
        modifier = Modifier
            .background(Color.DarkGray, RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable {
                viewModel.searchNews(text)
                navController.navigate("searchResult")
            }
    ) {
        Text(text = text, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.W600)
    }
}


@Composable
fun drawerTop_Before(navController: NavController, drawerState: DrawerState) {
    val scope = rememberCoroutineScope()
    TextButton(
        onClick = {
            navController.navigate("login")
            scope.launch {
                drawerState.close()
            }
        }, modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.inverseSurface,
            contentColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Text(
            text = stringResource(R.string.sign_up),
            fontSize = 24.sp
        )
    }

}

@Composable
fun drawerTop_After(name: String, image: String) {
    Image(

        painter = if (image != null) {
            rememberAsyncImagePainter(image)
        } else {
            painterResource(R.drawable.blank_profile)
        },
        contentDescription = "Profile",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .height(130.dp)
            .width(130.dp)
            .clip(shape = RoundedCornerShape(100.dp))
    )
    Text(
        name,
        style = MaterialTheme.typography.headlineSmall
    )
}