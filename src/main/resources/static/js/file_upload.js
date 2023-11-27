selectedFiles = [];

document.addEventListener('DOMContentLoaded', function () {
    const fileUploadPopup = document.getElementById('file-upload-popup');
    fileUploadPopup.style.display = 'none';
});

function toggleFileUploadPopup() {
    const popup = document.getElementById('file-upload-popup');
    popup.style.display = popup.style.display === 'none' ? 'block' : 'none';
}

function handleFileDrop(event) {
    event.preventDefault();
    const files = event.dataTransfer.files;
    for (const file of files) {
        if (isFileValid(file)) {
            selectedFiles.push(file);
        } else {
            const fileList = document.getElementById('selected-files');
            fileList.innerHTML = 'No files uploaded.';
        }
    }
    addFiles();
}

function addFiles() {
    const fileInput = document.getElementById('file-input');
    const hiddenInput = document.getElementById('hidden-input');
    let totalSize = 0;

    // Create a separate array for newly added files
    let newFiles = [];

    for (const file of fileInput.files) {

        // Validate file name
        if (!isFileValid(file)) {
            continue;
        }

        newFiles.push(file);
        totalSize += file.size;
    }

    // Merge the new files array with the selected files array, filtering duplicates
    selectedFiles = selectedFiles.concat(newFiles.filter(file => !selectedFiles.some(existingFile => existingFile.name === file.name)));

    // Clear file input to allow for adding same file again
    fileInput.value = '';

    // Check total size
    if (!isValidTotalSize(totalSize)) {
        alert('Total file size too large. It should be < ' + maxFileUploadingSize + ' MB.');
    } else {
        hiddenInput.value = selectedFiles.map(file => file.name).join(', ');
        updateSelectedFilesList();
        updateFileInputText();
    }
}

function isFileValid(file) {
    if (!isValidFileName(file.name)) {
        alert('Invalid file name: ' + file.name);
        return false;
    }

    if (!isValidFileSize(file.size)) {
        alert('File size too large: ' + file.name);
        return false;
    }

    if (!isValidFileType(file.name)) {
        alert('Invalid file type: ' + file.name);
        return false;
    }

    return true;
}

function isValidFileName(fileName) {

    // Check for double extensions
    const doubleExtensions = /\.(jpg|jpeg|png|gif|bmp|tiff|pdf|doc|docx|zip)\.(exe|bat|cmd|vbs|js)$/i;
    if (doubleExtensions.test(fileName)) {
        return false;
    }

    // Check for null bytes
    if (fileName.includes('\0')) {
        return false;
    }

    const dangerousExtensions = ['exe', 'bat', 'cmd', 'vbs', 'js'];
    const parts = fileName.split('.');

    const hasDangerousExtension = parts.some(part => dangerousExtensions.includes(part.toLowerCase()));

    if (hasDangerousExtension) {
        alert("Dangerous extension detected");
        return false;
    }

    return true;
}

function isValidFileSize(fileSize) {
    const maxSize = maxFileUploadingSize * 1024 * 1024; // MBs
    return fileSize <= maxSize;
}

function isValidTotalSize(totalSize) {
    const maxSize = maxFileUploadingSize * 1024 * 1024; // MBs
    return totalSize <= maxSize;
}

function isValidFileType(fileName) {
    const allowedFileTypes = /\.(jpg|jpeg|png|gif|bmp|tiff|pdf|doc|docx|zip|txt|json|html|css|csv)$/i;
    return allowedFileTypes.test(fileName);
}

function updateSelectedFilesList() {
    let fileList = document.getElementById('selected-files');

    if(selectedFiles.length === 0) {
        fileList.innerHTML = 'No files uploaded.';
        return;
    } else {
        fileList.innerHTML = '';
    }

    for (const file of selectedFiles) {
        const listItem = document.createElement('li');
        const removeButton = document.createElement('button');
        removeButton.classList.add('remove-button');
        removeButton.onclick = () => {
            selectedFiles = selectedFiles.filter(f => f !== file);
            updateSelectedFilesList();
            document.getElementById('hidden-input').value = selectedFiles.map(file => file.name).join(', ');
            updateFileInputText();
        };
        listItem.appendChild(removeButton);
        const fileNameSpan = document.createElement('span');
        fileNameSpan.textContent = file.name;
        listItem.appendChild(fileNameSpan);
        fileList.appendChild(listItem);
    }
}

function updateFileInputText() {
    const numFiles = selectedFiles.length;
    const label = (numFiles > 1) ? `${numFiles} files selected` : (numFiles === 1) ? '1 file selected' : '';

    const labelInput = document.getElementById('file-count');
    labelInput.innerHTML = label;
}

function uploadFiles() {
    const formData = new FormData();

    for (const file of selectedFiles) {
        formData.append('files', file);
    }

    if (selectedFiles.length === 0) {
        return;
    }

    fetch('/upload', {
        method: 'POST',
        body: formData
    })
        .then(response => response.json())
        .then(result => {
            selectedFiles = [];

            const fileList = document.getElementById('selected-files');
            fileList.innerHTML = 'No files uploaded.';

            const fileCount = document.getElementById('file-count');
            fileCount.innerHTML = '';

            sendFile(result);
        })
        .catch(error => {
            alert("Error during uploading the files.")
            console.error('Error:', error);
        });
}

function sendFile(files) {
    const chat_number = document.getElementById('chat-number');
    const receiver_email = document.getElementById('chat-about-user-email');
    var content = $('#message-text').val();
    const createdAt = new Date().toISOString().slice(0, 19);

    var message = {
        chatId: chat_number.innerText,
        text: content,
        senderEmail: meEmail,
        receiverEmail: receiver_email.innerText,
        createdAt: createdAt,
        files: files
    };

    stompClient.send("/app/chat", {}, JSON.stringify(message));
    $('#message-text').val('');

    displayMessageOnSenderSide(message);

    const fileUploadPopup = document.getElementById('file-upload-popup');
    fileUploadPopup.style.display = 'none';

    scrollToBottom();
}