<template>
    <!-- Loop through stock registrations grouped by areas -->
    <div v-for="(registrations, areaName) in stockRegistrations" :key="areaName" class="my-4">
        <!-- Title of each area, clickable to toggle showing the stock registrations -->
        <h3 class="font-bold text-lg mb-2 border-4 p-2 pl-4 cursor-pointer bg-gray-100 hover:bg-gray-200 transition"
            @click.stop="toggleArea(areaName)">
            {{ areaName }} <!-- Area name -->
        </h3>

        <!-- List of stock registrations, shown when the area is open -->
        <ul v-if="openStates[areaName]" class="ml-4 transition-all">
            <!-- Loop through category groups for this area -->
            <div v-for="categoryGroup in registrations" :key="categoryGroup.categoryName" class="my-4">
                <!-- Display the category name -->
                <h4>{{ categoryGroup.categoryName }}</h4>

                <!-- List of stock registrations for this category -->
                <li v-for="reg in categoryGroup.registrations" :key="reg.stockRegistrationId" @click.stop>
                    <!-- Display individual stock registration -->
                    <StockRegistrationItem :data="reg" @update="updateStockRegistrationData(reg.stockRegistrationId)" />
                </li>
            </div>
        </ul>
    </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { getAllStockRegistrationForInventoryInProgress, loadRegistrationById } from '@/controller/registrationController';
import { MESSAGE_HANDLER_TYPE } from '@/utils/constants/messageHandlerTypeText';
import { messageHandler } from '../../utils/messageManager/messageHandler';
import StockRegistrationItem from './StockRegistrationItem.vue';

const stockRegistrations = ref(""); // Holds the grouped stock registrations by area and category
const openStates = ref({}); // Holds the state of each area (open or closed)

// Function to load stock registration data
onMounted(loadData);

// Function to load stock registration data and group it by area and category
async function loadData() {
    try {
        let tempData = await getAllStockRegistrationForInventoryInProgress();
        const result = tempData.data.reduce((acc, registration) => {
            const areaName = registration.storageAreaName; // Get the area name
            const categoryName = registration.categoryName; // Get the category name
            if (!acc[areaName]) {
                acc[areaName] = [];
            }
            let categoryGroup = acc[areaName].find(group => group.categoryName === categoryName); // Check if category group exists
            if (!categoryGroup) {
                categoryGroup = { categoryName, registrations: [] };
                acc[areaName].push(categoryGroup);
            }
            categoryGroup.registrations.push(registration); // Add the registration to the category group
            return acc;
        }, {});
        stockRegistrations.value = (result); // Set the grouped stock registrations
    } catch (err) {
        messageHandler(err.message, MESSAGE_HANDLER_TYPE.ERROR);
    }
}

//Update view of a stock registration after updated
const updateStockRegistrationData = async (stockRegistrationId) => {
    try {
        if (stockRegistrationId) {
            const updatedRegistration = (await loadRegistrationById(stockRegistrationId)).data;
            for (const area in stockRegistrations.value) {
                const categories = stockRegistrations.value[area];
                for (const category of categories) {
                    const index = category.registrations.findIndex(r => r.stockRegistrationId === updatedRegistration.stockRegistrationId);
                    if (index !== -1) {
                        category.registrations[index] = updatedRegistration;
                        return;
                    }
                }
            }
        }
    }catch(err){
        messageHandler(err.message, MESSAGE_HANDLER_TYPE.ERROR)
    }
}

// Function to toggle the visibility of stock registrations for a specific area
const toggleArea = (areaName) => {
    openStates.value[areaName] = !openStates.value[areaName];
    for (let area in openStates.value) {
        if (area !== areaName) {
            openStates.value[area] = false;
        }
    }

};
</script>
