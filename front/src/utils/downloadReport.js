import { loadReport } from "@/controller/reportController";
import { messageHandler } from "@/utils/messageManager/messageHandler";

export async function downloadReport(idInventory) {

    try {
        const report = await loadReport(idInventory); // Load the report
        const contentDisposition = report.headers.get('Content-Disposition'); // Get the file name from the header
        const fileNameMatch = contentDisposition && contentDisposition.match(/filename="(.+)"/);
        const fileName = fileNameMatch ? fileNameMatch[1] : `rapport-inventaire-${idInventory}.csv`;

        const blob = await report.blob(); // Convert the response to a Blob
        const url = window.URL.createObjectURL(blob); // Create a URL for the Blob
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', fileName); // Set the download attribute

        document.body.appendChild(link);
        link.click(); // Trigger the download
        link.remove(); // Remove the link element
        window.URL.revokeObjectURL(url); // Revoke the Blob URL
    } catch(err){
        messageHandler("Une erreur est survenue durant la génération du rapport : " + err.message,"error")
    }
    
}