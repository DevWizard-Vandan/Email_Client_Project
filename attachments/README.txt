````text
============================================
ATTACHMENTS DIRECTORY
============================================

This directory stores all email attachments uploaded through the application.

STRUCTURE:
attachments/
├── user_1/
│   ├── 1736428800000_document.pdf
│   └── 1736428900000_image.png
├── user_2/
│   └── 1736429000000_report.docx
└── README.txt (this file)

NAMING CONVENTION:
{timestamp}_{original_filename}

- Timestamp: Unix milliseconds when file was uploaded
- Original filename: Preserved for user reference

SECURITY NOTES:
- Each user has a separate subdirectory
- File paths are stored in the Attachment table
- Maximum file size: 25 MB (configurable)
- Deleted attachments are removed from filesystem

MAINTENANCE:
- Old attachments can be archived/deleted manually
- Ensure sufficient disk space
- Regular backups recommended

============================================
DO NOT DELETE THIS DIRECTORY
The application creates it automatically if missing.
============================================
````
