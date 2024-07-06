# Requirements

The main goal of this application to provide an interface to the users, where they can:

- Find characters and their details of *The Lord of the Rings* universe.
- Learn more about the world of *The Lord of the Rings*.

![Use-case diagram][use_case_diagram]

## Screens

### 1. Splash Screen

On Android when the application is launched, first the *Splash Screen* is visible, which contains the application's
icon.

![Splash screen][splash_screen]

### 2. Character List Screen

This is the application's initial screen. When the first page loads, placeholder elements with shimmer animation are
visible. After loading, character names loaded in alphabetical order can be found in an infinite list. The infinite list
loads the next page when the user scrolls near the bottom of the currently loaded list, indicated by a
*CircularProgressIndicator* element at the bottom of the list showing that the application is loading the next page. On
Compact and Medium width screens, if the user taps on a list item, they navigate to the *Character Details Screen*. On
Expanded width devices, the character details are available next to the list. In this case, if the user
selects an item from the list, no navigation occurs; instead, the character details appear on the right side. On the
screen, using the additional actions button on the right side of the Top App Bar, a Modal Bottom Sheet opens on Compact
width devices, while a Menu opens on other devices, allowing the user to either open a Modal dialog for specifying a
name filter or navigate to the Information page with another selection. In the Modal dialog used for specifying a name
filter, the user can enter the name they want to search for, after which a Filter chip appears above the list. The
Filter chip displays the current filter condition and can be removed with the X icon on it. If the user provides an
empty string in the filter dialog, the existing filter condition, if any, is cleared. If loading a character list page
fails, the application indicates this by displaying a non-dismissive snackbar, with a button to retry loading. The list
can be reloaded using Pull-to-refresh.

![Character List Screen - Compact width - First page loading][character_list_screen_compact_width_first_page_loading]
![Character List Screen - Compact width][character_list_screen_compact_width]
![Character List Screen - Compact width - Filter chip][character_list_screen_compact_width_filter_chip]
![Character List Screen - Compact width - Actions Modal Bottom Sheet][character_list_screen_compact_width_actions_modal_bottom_sheet]
![Character List Screen - Medium width - First page loading][character_list_screen_medium_width_first_page_loading]
![Character List Screen - Medium width][character_list_screen_medium_width]
![Character List Screen - Medium width - Filter chip][character_list_screen_medium_width_filter_chip]
![Character List Screen - Medium and Expanded width - Actions Menu][character_list_screen_medium_and_expanded_width_actions_menu]
![Character List Screen - Expanded width - First page loading][character_list_screen_expanded_width_first_page_loading]
![Character List Screen - Expanded width - No character selected][character_list_screen_expanded_width_no_character_selected]
![Character List Screen - Expanded width][character_list_screen_expanded_width]
![Character List Screen - Expanded width - Filtered][character_list_screen_expanded_width_filtered]
![Character List Screen - Name Filter Dialog][character_list_screen_name_filter_dialog]
![Character List Screen - Loading error snackbar][character_list_screen_loading_error_snackbar]

### 3. Character Details Screen

The screen is accessible on Compact and Medium width devices from the *Character List Screen*. Using the navigation icon
on the top app bar, the user can navigate back to the *Character List Screen*. The screen displays detailed information
about a specific character, which includes:

- name
- height
- race
- gender
- birthdate
- partner
- death date
- spouse
- hair

During the initial loading, a version filled with placeholders featuring a shimmer animation is displayed. The screen
appears differently on Compact and Medium width devices: on Compact width devices, the data is displayed in a single
column, whereas on Medium width screens, the data is displayed in two columns. If loading the character details fails, a
snackbar appears on the screen, with a button to retry loading the page.

![Character Details Screen Compact Width Loading][character_details_screen_compact_width_loading]
![Character Details Screen Compact Width][character_details_screen_compact_width]
![Character Details Screen Medium Width Loading][character_details_screen_medium_width_loading]
![Character Details Screen Medium Width][character_details_screen_medium_width]
![Character Details Screen Loading Error Snackbar][character_details_screen_loading_error_snackbar]

### 4. Information Screen

On the information page, the user can read more information about the world of *The Lord of the Rings*. Using the
navigation button located on the top app bar, the user can navigate back to the *Character List Screen*.

![Information Screen - Compact Width][information_screen_compact_width]
![Information Screen - Medium Width][information_screen_medium_width]
![Information Screen - Expanded Width][information_screen_expanded_width]

## Themes

The application supports light and dark theme on every platform, and on Android [dynamic theme][android-dynamic-theme]
is also supported.

## Supported platforms

The client application is available as:

- a native **Android** application
    - Minimum API version: 21 (Android 5.0, Lollipop)
- a native **iOS** application
- a **desktop** application running on **JVM**

### Planned platforms to support in the future

- Web client using Kotlin WASM
    - will be implemented after [SQLDelight will support Kotlin WASM][sqldelight-kotlin-wasm-issue]

[use_case_diagram]: ./assets/use_case.svg

[splash_screen]: ./assets/Splash_Screen.jpg

[character_list_screen_compact_width_first_page_loading]: ./assets/Character_List_Screen_Compact_Width_First_Page_Loading.jpg

[character_list_screen_compact_width]: ./assets/Character_List_Screen_Compact_Width.jpg

[character_list_screen_compact_width_filter_chip]: ./assets/Character_List_Screen_Compact_Width_Filter_Chip.jpg

[character_list_screen_compact_width_actions_modal_bottom_sheet]: ./assets/Character_List_Screen_Compact_Width_Actions_Modal_Bottom_Sheet.jpg

[character_list_screen_medium_width_first_page_loading]: ./assets/Character_List_Screen_Medium_Width_First_Page_Loading.jpg

[character_list_screen_medium_width]: ./assets/Character_List_Screen_Medium_Width.jpg

[character_list_screen_medium_width_filter_chip]: ./assets/Character_List_Screen_Medium_Width_Filter_Chip.jpg

[character_list_screen_medium_and_expanded_width_actions_menu]: ./assets/Character_List_Screen_Medium_And_Expanded_Width_Actions_Menu.jpg

[character_list_screen_expanded_width_first_page_loading]: ./assets/Character_List_Screen_Expanded_Width_First_Page_Loading.jpg

[character_list_screen_expanded_width_no_character_selected]: ./assets/Character_List_Screen_Expanded_Width_No_Character_Selected.jpg

[character_list_screen_expanded_width]: ./assets/Character_List_Screen_Expanded_Width.jpg

[character_list_screen_expanded_width_filtered]: ./assets/Character_List_Screen_Expanded_Width_Filtered.jpg

[character_list_screen_name_filter_dialog]: ./assets/Character_List_Screen_Name_Filter_Dialog.jpg

[character_list_screen_loading_error_snackbar]: ./assets/Character_List_Screen_Loading_Error_Snackbar.jpg

[character_details_screen_compact_width_loading]: ./assets/Character_Details_Screen_Compact_Width_Loading.png

[character_details_screen_compact_width]: ./assets/Character_Details_Screen_Compact_Width.png

[character_details_screen_medium_width_loading]: ./assets/Character_Details_Screen_Medium_Width_Loading.png

[character_details_screen_medium_width]: ./assets/Character_Details_Screen_Medium_Width.png

[character_details_screen_loading_error_snackbar]: ./assets/Character_Details_Screen_Loading_Error_Snackbar.png

[information_screen_compact_width]: ./assets/Information_Screen_Compact_Width.png

[information_screen_medium_width]: ./assets/Information_Screen_Medium_Width.png

[information_screen_expanded_width]: ./assets/Information_Screen_Expanded_Width.png

[android-dynamic-theme]: https://developer.android.com/develop/ui/views/theming/dynamic-colors

[sqldelight-kotlin-wasm-issue]: https://github.com/cashapp/sqldelight/pull/4965