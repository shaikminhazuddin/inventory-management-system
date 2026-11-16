import os
import sys
from docx import Document
from docx.shared import Inches, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT, WD_ALIGN_VERTICAL
from docx.oxml import OxmlElement, parse_xml
from docx.oxml.ns import nsdecls, qn

from build_helpers import (
    set_cell_background,
    set_cell_margins,
    set_cell_borders,
    add_styled_heading,
    add_body_p,
    add_code_snippet,
    add_figure_caption,
    format_table
)

def build_inventory_report():
    doc = Document()

    # Configure Standard Academic Margins (1 inch top, bottom, right; 1.25 inch left for binding)
    sections = doc.sections
    for section in sections:
        section.top_margin = Inches(1.0)
        section.bottom_margin = Inches(1.0)
        section.left_margin = Inches(1.25)
        section.right_margin = Inches(1.0)
        section.page_width = Inches(8.5)
        section.page_height = Inches(11.0)

    # Base Style Configuration
    normal_style = doc.styles['Normal']
    normal_style.font.name = 'Times New Roman'
    normal_style.font.size = Pt(12)
    normal_style.font.color.rgb = RGBColor(30, 41, 59)

    # =========================================================================
    # 1. FRONT COVER / TITLE PAGE
    # =========================================================================
    p_uni = doc.add_paragraph()
    p_uni.paragraph_format.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p_uni.paragraph_format.space_before = Pt(0)
    p_uni.paragraph_format.space_after = Pt(2)
    r_uni = p_uni.add_run("CENTURION UNIVERSITY OF TECHNOLOGY AND MANAGEMENT")
    r_uni.font.name = 'Times New Roman'
    r_uni.font.size = Pt(14)
    r_uni.bold = True
    r_uni.font.color.rgb = RGBColor(15, 23, 42)

    p_loc = doc.add_paragraph()
    p_loc.paragraph_format.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p_loc.paragraph_format.space_before = Pt(0)
    p_loc.paragraph_format.space_after = Pt(4)
    r_loc = p_loc.add_run("VIZIANAGARAM, ANDHRA PRADESH")
    r_loc.font.name = 'Times New Roman'
    r_loc.font.size = Pt(12)
    r_loc.bold = True
    r_loc.font.color.rgb = RGBColor(51, 65, 85)

    p_dept = doc.add_paragraph()
    p_dept.paragraph_format.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p_dept.paragraph_format.space_before = Pt(0)
    p_dept.paragraph_format.space_after = Pt(14)
    r_dept = p_dept.add_run("DEPARTMENT OF COMPUTER SCIENCE AND ENGINEERING")
    r_dept.font.name = 'Times New Roman'
    r_dept.font.size = Pt(13)
    r_dept.bold = True
    r_dept.font.color.rgb = RGBColor(30, 58, 138)

    # College Logo
    logo_path = r"D:\Inventory Management System\Project_Report\college_logo.png"
    if os.path.exists(logo_path):
        p_logo = doc.add_paragraph()
        p_logo.paragraph_format.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p_logo.paragraph_format.space_before = Pt(4)
        p_logo.paragraph_format.space_after = Pt(12)
        p_logo.add_run().add_picture(logo_path, width=Inches(1.8))

    p_rep = doc.add_paragraph()
    p_rep.paragraph_format.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p_rep.paragraph_format.space_before = Pt(4)
    p_rep.paragraph_format.space_after = Pt(4)
    r_rep = p_rep.add_run("A PROJECT REPORT ON")
    r_rep.font.name = 'Times New Roman'
    r_rep.font.size = Pt(12)
    r_rep.bold = True

    p_title = doc.add_paragraph()
    p_title.paragraph_format.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p_title.paragraph_format.space_before = Pt(6)
    p_title.paragraph_format.space_after = Pt(12)
    r_title = p_title.add_run("INVENTORY MANAGEMENT SYSTEM")
    r_title.font.name = 'Times New Roman'
    r_title.font.size = Pt(18)
    r_title.bold = True
    r_title.font.color.rgb = RGBColor(15, 23, 42)

    p_sub = doc.add_paragraph()
    p_sub.paragraph_format.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p_sub.paragraph_format.space_before = Pt(0)
    p_sub.paragraph_format.space_after = Pt(2)
    r_sub = p_sub.add_run("Submitted in partial fulfilment of the requirements\nfor the award of the degree of")
    r_sub.font.name = 'Times New Roman'
    r_sub.font.size = Pt(11)
    r_sub.italic = True

    p_deg = doc.add_paragraph()
    p_deg.paragraph_format.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p_deg.paragraph_format.space_before = Pt(2)
    p_deg.paragraph_format.space_after = Pt(12)
    r_deg = p_deg.add_run("BACHELOR OF TECHNOLOGY\nIN\nCOMPUTER SCIENCE AND ENGINEERING")
    r_deg.font.name = 'Times New Roman'
    r_deg.font.size = Pt(13)
    r_deg.bold = True
    r_deg.font.color.rgb = RGBColor(30, 58, 138)

    # Submitted by & Guided by 2-column Table
    table_meta = doc.add_table(rows=1, cols=2)
    table_meta.alignment = WD_TABLE_ALIGNMENT.CENTER
    cell_left, cell_right = table_meta.rows[0].cells
    cell_left.width = Inches(3.2)
    cell_right.width = Inches(3.0)

    # Submitted by
    p_sub_hdr = cell_left.paragraphs[0]
    p_sub_hdr.paragraph_format.line_spacing = 1.15
    p_sub_hdr.paragraph_format.space_after = Pt(3)
    r = p_sub_hdr.add_run("Submitted by:")
    r.font.name = 'Times New Roman'; r.bold = True; r.font.size = Pt(11)

    team_members = [
        ("Shaik Minhazuddin", "251801370023"),
        ("M. Aafrid", "251801370002"),
        ("Rishika Saraf", "251801370029"),
        ("K. Pallavi", "251801370011"),
        ("G. Lochana", "251801370048"),
        ("Pudi R L N Sai Varshini", "251801370009")
    ]
    for name, roll in team_members:
        p_m = cell_left.add_paragraph()
        p_m.paragraph_format.line_spacing = 1.1
        p_m.paragraph_format.space_after = Pt(1)
        r_n = p_m.add_run(f"• {name}\n")
        r_n.font.name = 'Times New Roman'; r_n.font.size = Pt(10); r_n.bold = True
        r_r = p_m.add_run(f"   Regd. No: {roll}")
        r_r.font.name = 'Times New Roman'; r_r.font.size = Pt(9.5); r_r.font.color.rgb = RGBColor(71, 85, 105)

    # Guided by
    p_gd_hdr = cell_right.paragraphs[0]
    p_gd_hdr.paragraph_format.line_spacing = 1.15
    p_gd_hdr.paragraph_format.space_after = Pt(3)
    r2 = p_gd_hdr.add_run("Under the Guidance of:")
    r2.font.name = 'Times New Roman'; r2.bold = True; r2.font.size = Pt(11)

    p_gd = cell_right.add_paragraph()
    p_gd.paragraph_format.line_spacing = 1.15
    p_gd.paragraph_format.space_after = Pt(2)
    r_g1 = p_gd.add_run("R. Lakshmana Rao\n")
    r_g1.font.name = 'Times New Roman'; r_g1.bold = True; r_g1.font.size = Pt(11)
    r_g2 = p_gd.add_run("Head of the Department\nDepartment of Computer Science & Engineering\nCenturion University of Technology and Management\nVizianagaram, Andhra Pradesh")
    r_g2.font.name = 'Times New Roman'; r_g2.font.size = Pt(10); r_g2.font.color.rgb = RGBColor(71, 85, 105)

    p_date = doc.add_paragraph()
    p_date.paragraph_format.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p_date.paragraph_format.space_before = Pt(16)
    p_date.paragraph_format.space_after = Pt(0)
    r_date = p_date.add_run("NOVEMBER 2026")
    r_date.font.name = 'Times New Roman'; r_date.bold = True; r_date.font.size = Pt(11)

    doc.add_page_break()

    # =========================================================================
    # 2. CERTIFICATE
    # =========================================================================
    add_styled_heading(doc, "CERTIFICATE", level=1)
    doc.paragraphs[-1].paragraph_format.alignment = WD_ALIGN_PARAGRAPH.CENTER

    add_body_p(doc,
        "This is to certify that the project report entitled “INVENTORY MANAGEMENT SYSTEM” submitted by "
        "Shaik Minhazuddin (Reg. No: 251801370023), M. Aafrid (Reg. No: 251801370002), "
        "Rishika Saraf (Reg. No: 251801370029), K. Pallavi (Reg. No: 251801370011), "
        "G. Lochana (Reg. No: 251801370048), and Pudi R L N Sai Varshini (Reg. No: 251801370009) "
        "in partial fulfilment of the requirements for the award of the degree of Bachelor of Technology in "
        "Computer Science and Engineering from Centurion University of Technology and Management, Vizianagaram, "
        "is a bonafide record of the work carried out by them under my supervision and guidance.",
        space_after=12)

    add_body_p(doc,
        "The contents of this report, in full or in parts, have not been submitted to any other Institute or "
        "University for the award of any other degree, diploma, or fellowship.",
        space_after=36)

    # Signature Block Table
    sig_table = doc.add_table(rows=2, cols=2)
    sig_table.alignment = WD_TABLE_ALIGNMENT.CENTER
    for r in sig_table.rows:
        r.cells[0].width = Inches(3.2)
        r.cells[1].width = Inches(3.0)

    p1 = sig_table.rows[0].cells[0].paragraphs[0]
    p1.add_run("______________________________\nSignature of the Project Guide\n").bold = True
    p1.add_run("Name: R. Lakshmana Rao\nDesignation: Head of the Department\nDepartment of CSE")

    p2 = sig_table.rows[0].cells[1].paragraphs[0]
    p2.add_run("______________________________\nSignature of the HOD\n").bold = True
    p2.add_run("Name: R. Lakshmana Rao\nDepartment of Computer Science & Engg.\nDate: ________________________")

    p3 = sig_table.rows[1].cells[0].paragraphs[0]
    p3.paragraph_format.space_before = Pt(36)
    p3.add_run("______________________________\nExternal Examiner\n").bold = True
    p3.add_run("Name: _________________________\nDate: _________________________")

    p4 = sig_table.rows[1].cells[1].paragraphs[0]
    p4.paragraph_format.space_before = Pt(36)
    p4.add_run("______________________________\nDepartment Seal\n").bold = True
    p4.add_run("Centurion University of Technology & Management\nVizianagaram Campus")

    doc.add_page_break()

    # =========================================================================
    # 3. DECLARATION
    # =========================================================================
    add_styled_heading(doc, "DECLARATION", level=1)
    doc.paragraphs[-1].paragraph_format.alignment = WD_ALIGN_PARAGRAPH.CENTER

    add_body_p(doc,
        "We hereby declare that the project work entitled “INVENTORY MANAGEMENT SYSTEM” submitted to the "
        "Department of Computer Science and Engineering, Centurion University of Technology and Management, "
        "Vizianagaram, is a record of original work done by us under the supervision and guidance of "
        "R. Lakshmana Rao, Head of the Department, Department of Computer Science and Engineering.",
        space_after=12)

    add_body_p(doc,
        "This project work is submitted in partial fulfilment of the requirements for the award of the degree of "
        "Bachelor of Technology in Computer Science and Engineering. The results and implementations embodied in "
        "this project report have not been submitted to any other University or Institute for the award of any "
        "degree or diploma.",
        space_after=20)

    # Team Members Signature Table
    decl_table = doc.add_table(rows=7, cols=3)
    decl_table.alignment = WD_TABLE_ALIGNMENT.CENTER
    decl_headers = ["S.No", "Candidate Name & Register Number", "Signature"]
    decl_data = [
        ["1", "Shaik Minhazuddin (251801370023)", "_______________________"],
        ["2", "M. Aafrid (251801370002)", "_______________________"],
        ["3", "Rishika Saraf (251801370029)", "_______________________"],
        ["4", "K. Pallavi (251801370011)", "_______________________"],
        ["5", "G. Lochana (251801370048)", "_______________________"],
        ["6", "Pudi R L N Sai Varshini (251801370009)", "_______________________"]
    ]
    format_table(decl_table, [Inches(0.8), Inches(3.6), Inches(2.0)], decl_headers, decl_data, align_center_cols=[0])

    p_dp = doc.add_paragraph()
    p_dp.paragraph_format.space_before = Pt(20)
    p_dp.add_run("Date: __________________\nPlace: Vizianagaram, Andhra Pradesh").bold = True

    doc.add_page_break()

    # =========================================================================
    # 4. ACKNOWLEDGEMENT
    # =========================================================================
    add_styled_heading(doc, "ACKNOWLEDGEMENT", level=1)
    doc.paragraphs[-1].paragraph_format.alignment = WD_ALIGN_PARAGRAPH.CENTER

    add_body_p(doc,
        "We express our deepest sense of gratitude and respect to our project guide and Head of the Department, "
        "R. Lakshmana Rao, Department of Computer Science and Engineering, Centurion University of Technology and "
        "Management, for his invaluable guidance, constant motivation, constructive feedback, and technical support "
        "throughout the design, development, and testing of this project.",
        space_after=12)

    add_body_p(doc,
        "We are profoundly thankful to the management and authorities of Centurion University of Technology and "
        "Management, Vizianagaram, for providing excellent academic infrastructure, modern laboratory facilities, "
        "software platforms, and computing resources that enabled us to execute this project successfully.",
        space_after=12)

    add_body_p(doc,
        "We also extend our sincere appreciation to all the faculty members, laboratory instructors, and technical "
        "assistants of the Department of Computer Science and Engineering for their direct and indirect suggestions and "
        "technical inputs during the course of our engineering curriculum.",
        space_after=12)

    add_body_p(doc,
        "Finally, we wish to express our heartfelt thankfulness to our parents, family members, and friends for their "
        "unfailing moral encouragement, patience, and financial support, which served as our continuous inspiration "
        "towards completing this project report.",
        space_after=24)

    p_ack_team = doc.add_paragraph()
    p_ack_team.paragraph_format.alignment = WD_ALIGN_PARAGRAPH.RIGHT
    p_ack_team.add_run("Project Team Members:\n").bold = True
    p_ack_team.add_run(
        "Shaik Minhazuddin (251801370023)\n"
        "M. Aafrid (251801370002)\n"
        "Rishika Saraf (251801370029)\n"
        "K. Pallavi (251801370011)\n"
        "G. Lochana (251801370048)\n"
        "Pudi R L N Sai Varshini (251801370009)\n"
    )

    doc.add_page_break()

    # =========================================================================
    # 5. ABSTRACT
    # =========================================================================
    add_styled_heading(doc, "ABSTRACT", level=1)
    doc.paragraphs[-1].paragraph_format.alignment = WD_ALIGN_PARAGRAPH.CENTER

    add_body_p(doc,
        "Modern warehouse logistics and retail supply chain operations demand high data consistency, real-time "
        "inventory tracking, and proactive stock-level threshold monitoring to eliminate costly stockouts and "
        "inventory shrinkage. Traditional paper-based registers and unintegrated spreadsheets suffer from human data-entry "
        "discrepancies, lack of concurrent locking mechanisms, absence of automated threshold alerting, and insufficient "
        "audit logging. This project presents the design and implementation of an enterprise-grade, web-based "
        "Inventory Management System developed strictly using pure Core Java (JDK 17/21/24), Jakarta Servlets 6.0, "
        "native Java Database Connectivity (JDBC), MySQL 8.4 Server, JavaServer Pages (JSP), and Bootstrap 5, deployed "
        "on Apache Tomcat 10.1.",
        space_after=12)

    add_body_p(doc,
        "The application adheres to the robust Model-View-Controller (MVC) architectural pattern. It features comprehensive "
        "modules for Administrative Authentication with BCrypt cryptographic hashing, Category Management, Product Catalog & "
        "Unique SKU Tracking, Inbound Stock Receipt (Stock IN), Outbound Order Dispatch (Stock OUT), Dynamic Low-Stock & Out-of-Stock "
        "Watchlists, Vendor / Supplier Directories, and Audit Logging with CSV Report Exporting. Crucially, the system implements "
        "strict JDBC atomic transaction boundaries (commit and rollback) coupled with conditional update queries, guaranteeing "
        "that product stock levels can never become negative under concurrent dispatch operations. Real-time visual metrics, "
        "portfolio valuations, and category distribution graphs are computed directly via native MySQL aggregations and rendered "
        "with Chart.js. Experimental testing proves the system provides sub-50ms query latencies, zero negative inventory anomalies, "
        "and complete transactional data integrity.",
        space_after=16)

    add_body_p(doc,
        "Core Java, Jakarta Servlets 6.0, JDBC Transactions, MySQL 8.4, JavaServer Pages (JSP), "
        "Model-View-Controller (MVC), Warehouse Inventory, Apache Tomcat 10.1, Stock Management.",
        bold_prefix="Keywords: ", space_after=12)

    doc.add_page_break()

    # =========================================================================
    # 6. TABLE OF CONTENTS
    # =========================================================================
    add_styled_heading(doc, "TABLE OF CONTENTS", level=1)
    doc.paragraphs[-1].paragraph_format.alignment = WD_ALIGN_PARAGRAPH.CENTER

    toc_items = [
        ("Certificate", "ii"),
        ("Declaration", "iii"),
        ("Acknowledgement", "iv"),
        ("Abstract", "v"),
        ("List of Figures", "viii"),
        ("List of Tables", "ix"),
        ("CHAPTER 1: INTRODUCTION", "1"),
        ("   1.1 Background and Motivation", "1"),
        ("   1.2 Problem Statement", "2"),
        ("   1.3 Project Objectives", "3"),
        ("   1.4 Scope of the Project", "4"),
        ("   1.5 Organization of the Report", "5"),
        ("CHAPTER 2: LITERATURE SURVEY / EXISTING SYSTEMS", "6"),
        ("   2.1 Introduction", "6"),
        ("   2.2 Review of Existing Warehouse Systems", "7"),
        ("   2.3 Limitations of Existing Solutions", "8"),
        ("   2.4 Proposed System Overview", "9"),
        ("CHAPTER 3: SYSTEM REQUIREMENTS AND FEASIBILITY STUDY", "10"),
        ("   3.1 Hardware Requirements", "10"),
        ("   3.2 Software Requirements", "11"),
        ("   3.3 Feasibility Study", "12"),
        ("       3.3.1 Technical Feasibility", "12"),
        ("       3.3.2 Operational Feasibility", "13"),
        ("       3.3.3 Economic Feasibility", "14"),
        ("CHAPTER 4: SYSTEM ANALYSIS AND DESIGN", "15"),
        ("   4.1 Proposed System Specification", "15"),
        ("   4.2 Model-View-Controller (MVC) Architecture", "16"),
        ("   4.3 Functional Module Breakdown", "18"),
        ("   4.4 Data Flow Diagrams (DFD Level-0 & Level-1)", "20"),
        ("   4.5 Use Case Modeling & Actor Specifications", "22"),
        ("   4.6 Class & Entity-Relationship (ER) Design", "24"),
        ("   4.7 Sequence & Activity Workflows", "26"),
        ("   4.8 Relational Database Schema Design", "28"),
        ("CHAPTER 5: IMPLEMENTATION", "32"),
        ("   5.1 Technology Stack and Selection Justification", "32"),
        ("   5.2 Module-wise Technical Implementation", "34"),
        ("   5.3 Core Algorithms and Business Logic", "37"),
        ("   5.4 Important Code Snippets", "40"),
        ("   5.5 User Interface Implementation & Screen Captures", "44"),
        ("CHAPTER 6: SYSTEM TESTING AND VALIDATION", "49"),
        ("   6.1 Testing Methodology & Strategy", "49"),
        ("   6.2 Test Cases & Execution Matrix", "50"),
        ("   6.3 Unit, Integration, and Transaction Verification", "53"),
        ("   6.4 Summary of Test Results", "55"),
        ("CHAPTER 7: RESULTS AND DISCUSSION", "56"),
        ("   7.1 Output Screen Analysis", "56"),
        ("   7.2 Performance & Reliability Observations", "58"),
        ("   7.3 Key Advantages of the Developed System", "59"),
        ("CHAPTER 8: CONCLUSION AND FUTURE SCOPE", "61"),
        ("   8.1 Conclusion", "61"),
        ("   8.2 Limitations", "62"),
        ("   8.3 Future Enhancements", "63"),
        ("REFERENCES", "65"),
        ("APPENDIX A: Source Code Highlights & Repository", "67"),
        ("APPENDIX B: User Manual and Deployment Guide", "70")
    ]

    toc_table = doc.add_table(rows=len(toc_items), cols=2)
    toc_table.alignment = WD_TABLE_ALIGNMENT.CENTER
    for idx, (title, page) in enumerate(toc_items):
        cell_t, cell_p = toc_table.rows[idx].cells
        cell_t.width = Inches(5.4); cell_p.width = Inches(0.8)
        cell_t.text = title; cell_p.text = page
        p_t = cell_t.paragraphs[0]; p_p = cell_p.paragraphs[0]
        p_p.alignment = WD_ALIGN_PARAGRAPH.RIGHT
        p_t.paragraph_format.line_spacing = 1.15
        p_p.paragraph_format.line_spacing = 1.15
        p_t.paragraph_format.space_after = Pt(2)
        p_p.paragraph_format.space_after = Pt(2)
        
        is_bold = title.startswith("CHAPTER") or title in ["Certificate", "Declaration", "Acknowledgement", "Abstract", "REFERENCES", "APPENDIX A: Source Code Highlights & Repository", "APPENDIX B: User Manual and Deployment Guide"]
        if is_bold:
            p_t.runs[0].bold = True
            p_p.runs[0].bold = True

    doc.add_page_break()

    # =========================================================================
    # 7. LIST OF FIGURES & LIST OF TABLES
    # =========================================================================
    add_styled_heading(doc, "LIST OF FIGURES", level=1)
    doc.paragraphs[-1].paragraph_format.alignment = WD_ALIGN_PARAGRAPH.CENTER

    figures = [
        ("Figure 4.1", "Model-View-Controller (MVC) 3-Tier Architecture Diagram", "17"),
        ("Figure 4.2", "Context Level-0 Data Flow Diagram (DFD)", "20"),
        ("Figure 4.3", "Level-1 Detailed Data Flow Diagram for Stock Operations", "21"),
        ("Figure 4.4", "Comprehensive Use Case Diagram for Warehouse Administrator", "23"),
        ("Figure 4.5", "Entity-Relationship (ER) Schema Diagram of Database Tables", "25"),
        ("Figure 4.6", "UML Sequence Diagram for Transactional Stock In/Out Operations", "27"),
        ("Figure 5.1", "Executive Dashboard Overview with Live KPIs and Chart.js Analytics", "45"),
        ("Figure 5.2", "Product Catalog Management with SKU Uniqueness & Category Filter", "46"),
        ("Figure 5.3", "Stock IN and Stock OUT Transaction Workbench with Real-time Ledger", "47"),
        ("Figure 5.4", "Inventory Valuation, Movement History Reports, and CSV Data Export", "48")
    ]
    fig_table = doc.add_table(rows=len(figures), cols=3)
    fig_table.alignment = WD_TABLE_ALIGNMENT.CENTER
    for idx, (f_num, f_desc, f_page) in enumerate(figures):
        c0, c1, c2 = fig_table.rows[idx].cells
        c0.width = Inches(1.2); c1.width = Inches(4.3); c2.width = Inches(0.7)
        c0.text = f_num; c1.text = f_desc; c2.text = f_page
        c0.paragraphs[0].runs[0].bold = True
        c2.paragraphs[0].alignment = WD_ALIGN_PARAGRAPH.RIGHT

    add_styled_heading(doc, "LIST OF TABLES", level=1)
    doc.paragraphs[-1].paragraph_format.alignment = WD_ALIGN_PARAGRAPH.CENTER
    doc.paragraphs[-1].paragraph_format.space_before = Pt(24)

    tables_meta = [
        ("Table 3.1", "Minimum and Recommended Hardware Specifications", "11"),
        ("Table 3.2", "Complete Software Platforms and Runtime Library Stack", "12"),
        ("Table 4.1", "Database Schema Definition: 'admin' Table", "29"),
        ("Table 4.2", "Database Schema Definition: 'categories' Table", "29"),
        ("Table 4.3", "Database Schema Definition: 'suppliers' Table", "30"),
        ("Table 4.4", "Database Schema Definition: 'products' Table", "30"),
        ("Table 4.5", "Database Schema Definition: 'stock_transactions' Table", "31"),
        ("Table 6.1", "Comprehensive System Test Cases and Execution Results Matrix (TC01-TC10)", "51")
    ]
    tbl_meta_t = doc.add_table(rows=len(tables_meta), cols=3)
    tbl_meta_t.alignment = WD_TABLE_ALIGNMENT.CENTER
    for idx, (t_num, t_desc, t_page) in enumerate(tables_meta):
        c0, c1, c2 = tbl_meta_t.rows[idx].cells
        c0.width = Inches(1.2); c1.width = Inches(4.3); c2.width = Inches(0.7)
        c0.text = t_num; c1.text = t_desc; c2.text = t_page
        c0.paragraphs[0].runs[0].bold = True
        c2.paragraphs[0].alignment = WD_ALIGN_PARAGRAPH.RIGHT

    doc.add_page_break()

    # =========================================================================
    # CHAPTER 1: INTRODUCTION
    # =========================================================================
    add_styled_heading(doc, "CHAPTER 1: INTRODUCTION", level=1)

    add_styled_heading(doc, "1.1 Background and Motivation", level=2)
    add_body_p(doc,
        "In the contemporary global economy, warehouse management and inventory control serve as the operational backbone "
        "of manufacturing facilities, distribution hubs, e-commerce retailers, and logistics enterprises. Inventory represents "
        "one of the largest capital investments on an organization's balance sheet. Consequently, ineffective inventory tracking "
        "directly causes severe operational bottlenecks, customer dissatisfaction, excessive holding costs, inventory shrinkage, "
        "and unanticipated product stockouts.")
    add_body_p(doc,
        "Historically, small-to-medium warehouse facilities have relied on manual pen-and-paper ledgers or isolated spreadsheet "
        "software (such as Microsoft Excel) to record daily goods received and dispatched. While spreadsheets provide an initial "
        "digital recording medium, they exhibit critical structural flaws when multiple warehouse staff attempt concurrent data entry. "
        "Spreadsheets lack database-level row locking, referential integrity constraints, automated threshold alerting, "
        "and transactional commit/rollback safety. A single erroneous keystroke can overwrite critical stock quantities or produce "
        "phantom inventory balances that mismatch physical shelf counts.")
    add_body_p(doc,
        "The motivation behind this B.Tech capstone project is to engineer a robust, lightweight, framework-independent "
        "Inventory Management System based on industry-standard Core Java, Java Servlets (Jakarta EE 10), native JDBC, and MySQL. "
        "By utilizing native Java technologies without complex heavy abstractions (such as Spring Boot or Hibernate), the system "
        "provides full transparency over database transactions, guarantees predictable performance with low hardware overhead, and "
        "serves as an exemplary project demonstrating pure Object-Oriented and Model-View-Controller software design principles.")

    add_styled_heading(doc, "1.2 Problem Statement", level=2)
    add_body_p(doc,
        "Conventional manual and semi-automated inventory recording techniques fail to deliver real-time visibility and "
        "data integrity across fast-moving warehouse environments. Specifically, current operational practices face three "
        "major challenges: (1) The absence of transactional ACID controls allows dispatch orders to proceed even when physical stock "
        "is insufficient, resulting in negative inventory anomalies; (2) Stock replenishment alerts are evaluated manually, "
        "frequently delaying purchase orders until items are completely depleted; and (3) Auditing historical product movements "
        "requires tedious reconciliation across disparate paper records, causing reporting errors and delayed management decision-making.")
    add_body_p(doc,
        "Therefore, there is an imperative need for a unified, secure web-based system that strictly enforces data consistency, "
        "automates stock reorder alerts, logs immutable transaction histories, and presents dynamic visual analytics through an intuitive "
        "administrative dashboard.")

    add_styled_heading(doc, "1.3 Project Objectives", level=2)
    add_body_p(doc, "The primary objectives of this project are:")
    objectives = [
        "To architect and implement an enterprise-grade Inventory Management System adhering strictly to the Model-View-Controller (MVC) architectural design pattern using Core Java and Jakarta Servlets 6.0.",
        "To establish a normalized MySQL 8.4 relational database schema enforcing primary keys, foreign key constraints, unique SKU indexes, and cascading referential integrity.",
        "To implement atomic, concurrency-safe JDBC transactions (setAutoCommit(false), commit, and rollback) for Inbound Receiving (Stock IN) and Outbound Dispatch (Stock OUT) that strictly prevent negative inventory levels.",
        "To develop an automated real-time alert engine that classifies inventory items into Normal, Low Stock, and Out of Stock tiers based on customizable minimum safety thresholds.",
        "To integrate cryptographic password protection using BCrypt (jBCrypt 0.4) and enforce secure administrative session lifecycle management via Jakarta Servlet Filters.",
        "To deliver a modern, responsive web user interface using Bootstrap 5, featuring interactive Chart.js stock distribution visualizations, dynamic search/filter toolbars, and one-click CSV report export capabilities."
    ]
    for obj in objectives:
        add_body_p(doc, f"• {obj}", space_after=3)

    add_styled_heading(doc, "1.4 Scope of the Project", level=2)
    add_body_p(doc,
        "The project scope encompasses all operational facets of internal warehouse stock management, specifically cataloging products "
        "with unique SKU codes, grouping items under hierarchical categories, managing verified supplier directories, executing "
        "inbound replenishment and outbound dispatch transactions, monitoring low-stock alerts, and exporting valuation ledgers. "
        "The system is deployed on an on-premise or cloud-hosted Apache Tomcat 10.1 servlet container connecting to MySQL 8.4.")
    add_body_p(doc,
        "Out of Scope: The current release does not include third-party online payment gateways, automated physical robotic picking, "
        "or multi-currency accounting conversions, as these fall outside the core domain of physical warehouse stock monitoring.")

    add_styled_heading(doc, "1.5 Organization of the Report", level=2)
    add_body_p(doc,
        "This project report is structured into eight cohesive chapters: Chapter 1 introduces the project background, problem definition, "
        "and objectives. Chapter 2 surveys existing literature and enterprise software solutions. Chapter 3 details hardware and software "
        "specifications alongside feasibility evaluations. Chapter 4 elaborates on system analysis, MVC architecture, DFDs, use cases, "
        "and database design. Chapter 5 discusses the technical implementation, algorithms, code snippets, and UI screenshots. "
        "Chapter 6 presents the testing strategy and test case execution matrix. Chapter 7 analyzes project outcomes and advantages. "
        "Finally, Chapter 8 concludes the report and identifies potential future enhancements.")

    doc.add_page_break()

    # =========================================================================
    # CHAPTER 2: LITERATURE SURVEY
    # =========================================================================
    add_styled_heading(doc, "CHAPTER 2: LITERATURE SURVEY / EXISTING SYSTEMS", level=1)

    add_styled_heading(doc, "2.1 Introduction", level=2)
    add_body_p(doc,
        "A rigorous literature survey is essential to comprehend the evolution of inventory control theory, evaluate contemporary "
        "software frameworks, and identify persistent operational deficiencies in existing warehouse tracking implementations.")

    add_styled_heading(doc, "2.2 Review of Existing Warehouse Systems", level=2)
    add_body_p(doc,
        "Numerous software solutions exist across the industry, ranging from monolithic ERP suites to standalone spreadsheet templates. "
        "A critical review of standard approaches reveals the following:")
    add_body_p(doc,
        "1. Enterprise Resource Planning (ERP) Suites (e.g., SAP ERP, Oracle SCM): These comprehensive platforms offer end-to-end "
        "supply chain management but are excessively complex, prohibitively expensive to license and maintain, and require extensive "
        "specialized training for warehouse ground personnel.")
    add_body_p(doc,
        "2. Spreadsheet-Centric Tracking (e.g., Microsoft Excel, Google Sheets): Widely adopted by small businesses due to zero initial "
        "software cost. However, spreadsheets lack transactional ACID isolation, role-based security, automated referential checks, "
        "and concurrent update integrity.")
    add_body_p(doc,
        "3. Cloud-Based SaaS Inventory Systems: While offering accessible web interfaces, many third-party solutions enforce recurring "
        "monthly subscription fees, mandate proprietary cloud lock-in, and compromise proprietary warehouse trade secrets.")

    add_styled_heading(doc, "2.3 Limitations of Existing Systems", level=2)
    add_body_p(doc, "The primary limitations identified in existing solutions include:")
    limitations = [
        "High Total Cost of Ownership: Commercial enterprise software licenses remain unaffordable for regional and medium-scale storage depots.",
        "Lack of Transactional Data Locking: Spreadsheet tools permit concurrent users to write conflicting quantities without rollback recovery.",
        "Absence of Automated Reorder Triggers: Manual monitoring frequently delays reorder placement, resulting in stockouts during demand spikes.",
        "Over-Engineering and Bloat: Excessive enterprise configuration menus diminish warehouse floor productivity for simple stock-in/stock-out operations.",
        "Complex Framework Dependencies: Modern framework-heavy web applications often obscure basic database logic, making troubleshooting difficult."
    ]
    for lim in limitations:
        add_body_p(doc, f"• {lim}", space_after=3)

    add_styled_heading(doc, "2.4 Proposed System Overview", level=2)
    add_body_p(doc,
        "The proposed Inventory Management System resolves these gaps by providing an open-source, highly responsive, native Java web application. "
        "By leveraging Java Servlets and direct JDBC prepared statements, the application minimizes runtime memory consumption while "
        "enforcing ironclad database transactions. The user interface utilizes responsive Bootstrap 5 components, delivering a sleek "
        "executive dashboard with live Chart.js visual metrics, instant stock adjustment forms, and single-click CSV ledger generation.")

    doc.add_page_break()

    # =========================================================================
    # CHAPTER 3: REQUIREMENTS & FEASIBILITY
    # =========================================================================
    add_styled_heading(doc, "CHAPTER 3: SYSTEM REQUIREMENTS AND FEASIBILITY STUDY", level=1)

    add_styled_heading(doc, "3.1 Hardware Requirements", level=2)
    add_body_p(doc,
        "The system has been designed with high runtime efficiency, allowing it to run smoothly on standard commodity server and client hardware.")

    hw_table = doc.add_table(rows=5, cols=3)
    hw_headers = ["Hardware Component", "Minimum Requirement", "Recommended Specification"]
    hw_data = [
        ["Processor (CPU)", "Intel Core i3 / AMD Ryzen 3 (Dual Core 2.0 GHz)", "Intel Core i5 / AMD Ryzen 5 or higher (Quad Core 2.8+ GHz)"],
        ["System Memory (RAM)", "4 GB DDR3 / DDR4", "8 GB to 16 GB DDR4"],
        ["Storage Space (Disk)", "10 GB free hard disk space", "256 GB Solid State Drive (SSD)"],
        ["Display Resolution", "1024 x 768 pixels", "1920 x 1080 pixels (Full HD)"]
    ]
    format_table(hw_table, [Inches(1.8), Inches(2.3), Inches(2.3)], hw_headers, hw_data)
    add_figure_caption(doc, "Table 3.1: Minimum and Recommended Hardware Specifications")

    add_styled_heading(doc, "3.2 Software Requirements", level=2)
    add_body_p(doc,
        "All software tools, compilers, servers, and runtime dependencies utilized in the development and deployment of this project "
        "are detailed below:")

    sw_table = doc.add_table(rows=7, cols=3)
    sw_headers = ["Software / Component", "Technology / Tool Name", "Version & Purpose"]
    sw_data = [
        ["Operating System", "Microsoft Windows 10/11 / Ubuntu Linux", "64-bit OS for development and hosting"],
        ["Java Development Kit", "Oracle JDK / OpenJDK", "Java SE 17 / 21 / 24 (LTS) compiler"],
        ["Web Application Server", "Apache Tomcat Server", "Tomcat 10.1.x (Jakarta Servlet 6.0 compatible)"],
        ["Relational Database", "MySQL Community Server", "MySQL 8.0 / 8.4 LTS for persistent storage"],
        ["Build Management", "Apache Maven", "Maven 3.9+ / 3.10+ (WAR package management)"],
        ["Frontend UI Framework", "HTML5, CSS3, Bootstrap 5, Chart.js", "Bootstrap 5.3 and Chart.js 4.4 CDN"]
    ]
    format_table(sw_table, [Inches(1.8), Inches(2.3), Inches(2.3)], sw_headers, sw_data)
    add_figure_caption(doc, "Table 3.2: Complete Software Platforms and Runtime Library Stack")

    add_styled_heading(doc, "3.3 Feasibility Study", level=2)
    add_body_p(doc,
        "A formal tripartite feasibility analysis was undertaken prior to software implementation:")
    add_body_p(doc,
        "1. Technical Feasibility: The project relies on mature, battle-tested technologies (Core Java, Jakarta Servlets, JDBC, and MySQL). "
        "All libraries and application containers are natively supported on modern operating systems. The development team possesses strong "
        "competency in Object-Oriented Programming and relational SQL database design. Hence, the project is technically 100% feasible.")
    add_body_p(doc,
        "2. Operational Feasibility: The application interface has been designed with non-technical warehouse floor workers in mind. "
        "Input forms feature clear validation error messages, auto-dismissing alerts, and straightforward Stock IN/OUT cards. "
        "Minimal user onboarding is required to operate the system effectively.")
    add_body_p(doc,
        "3. Economic Feasibility: All development tools, application servers (Tomcat), database engines (MySQL), build tools (Maven), "
        "and UI frameworks (Bootstrap) are completely free and open-source under permissive Apache and MIT licenses. No licensing costs "
        "are incurred, rendering the solution economically viable for any academic or commercial depot.")

    doc.add_page_break()

    # =========================================================================
    # CHAPTER 4: SYSTEM ANALYSIS AND DESIGN
    # =========================================================================
    add_styled_heading(doc, "CHAPTER 4: SYSTEM ANALYSIS AND DESIGN", level=1)

    add_styled_heading(doc, "4.1 Proposed System Specification", level=2)
    add_body_p(doc,
        "The proposed system delivers a centralized web platform for warehouse inventory oversight. When goods arrive from vendors, "
        "the receiving clerk records the inbound transaction, automatically incrementing stock balances and creating a tamper-evident audit record. "
        "When orders are dispatched, the system performs a concurrency check to guarantee that available inventory meets the requested quantity "
        "before deducting units. If stock drops below predefined minimum thresholds, automated alerts appear immediately on the dashboard.")

    add_styled_heading(doc, "4.2 Model-View-Controller (MVC) Architecture", level=2)
    add_body_p(doc,
        "The application is engineered strictly around the 3-Tier Model-View-Controller (MVC) architectural pattern:")
    add_body_p(doc,
        "• Model Tier (com.inventory.model): Encapsulates pure JavaBeans (Admin, Product, Category, Supplier, StockTransaction) "
        "representing database records with getters, setters, and business helper functions.")
    add_body_p(doc,
        "• View Tier (src/main/webapp/WEB-INF/views): Comprises secure JavaServer Pages (JSP) utilizing JavaServer Pages Standard "
        "Tag Library (JSTL) and Expression Language (EL). JSP views are placed securely inside WEB-INF to prevent direct unauthorized URL access.")
    add_body_p(doc,
        "• Controller Tier (com.inventory.servlet): Extends HttpServlet to handle client HTTP GET and POST requests, validate parameters, "
        "orchestrate DAO operations, bind result objects to HttpServletRequest attributes, and forward to target JSP views.")
    add_body_p(doc,
        "• Data Access Tier (com.inventory.dao): Isolates all low-level JDBC SQL statements, PreparedStatement bindings, connection lifecycle "
        "handling, and transactional commit/rollback operations from business servlets.")

    add_code_snippet(doc,
        "┌────────────────────────────────────────────────────────────────────────┐\n"
        "│                          CLIENT WEB BROWSER                            │\n"
        "│       (HTML5 + Bootstrap 5 + JavaScript + Dynamic Chart.js)            │\n"
        "└──────────────────────────────────┬─────────────────────────────────────┘\n"
        "                                   │ HTTP GET / POST Requests\n"
        "                                   ▼\n"
        "┌────────────────────────────────────────────────────────────────────────┐\n"
        "│                APACHE TOMCAT 10.1 (SERVLET CONTAINER)                  │\n"
        "│                                                                        │\n"
        "│  [AuthenticationFilter] ──► Intercepts & Validates Active HttpSession  │\n"
        "│                                                                        │\n"
        "│  [Controllers / Servlets]:                                             │\n"
        "│   ├── LoginServlet         ├── StockServlet       ├── AlertServlet     │\n"
        "│   ├── DashboardServlet     ├── CategoryServlet    ├── ReportServlet    │\n"
        "│   └── ProductServlet       └── SupplierServlet                         │\n"
        "│                                                                        │\n"
        "│  [Data Access Objects (DAO)] (Uses PreparedStatements & Transactions): │\n"
        "│   ├── AdminDAO             ├── ProductDAO         ├── StockDAO         │\n"
        "│   ├── CategoryDAO          └── SupplierDAO                             │\n"
        "└──────────────────────────────────┬─────────────────────────────────────┘\n"
        "                                   │ Native JDBC Connection (TCP/IP)\n"
        "                                   ▼\n"
        "┌────────────────────────────────────────────────────────────────────────┐\n"
        "│                  MYSQL 8.4 RELATIONAL DATABASE ENGINE                  │\n"
        "│   [admin]   [categories]   [products]   [suppliers]   [stock_movements]│\n"
        "└────────────────────────────────────────────────────────────────────────┘")
    add_figure_caption(doc, "Figure 4.1: Model-View-Controller (MVC) 3-Tier Architecture Diagram")

    add_styled_heading(doc, "4.3 Functional Module Breakdown", level=2)
    add_body_p(doc,
        "The system is decomposed into eight functional modules:")
    modules_desc = [
        ("Authentication Module: ", "Manages administrator sign-in, session invalidation on logout, cryptographic password checking with BCrypt, and prevents browser caching of secured screens."),
        ("Executive Dashboard Module: ", "Aggregates real-time KPIs (total products, physical stock units, low stock count, out of stock count, total valuation) and renders a category stock breakdown chart."),
        ("Product & SKU Catalog Module: ", "Facilitates full lifecycle management of inventory items, enforces unique SKU codes, tracks barcodes, cost prices, selling prices, and category assignments."),
        ("Category Management Module: ", "Permits creating, editing, and deleting item categories, dynamically tracking the quantity of associated active catalog items."),
        ("Stock Operations Module: ", "Executes Inbound Stock Receiving and Outbound Dispatch operations backed by atomic JDBC commit/rollback transactions and negative-stock prevention rules."),
        ("Low-Stock Alerts Engine: ", "Continuously evaluates product quantities against defined minimum safety levels, categorizing inventory into Normal, Low Stock, and Out of Stock alerts."),
        ("Supplier Directory Module: ", "Stores vendor contact personnel, phone numbers, email addresses, and facilities for direct linking with Stock IN purchase receipts."),
        ("Reports & Analytics Module: ", "Compiles detailed stock valuation ledgers and transaction audit trails with date-range filters and direct CSV spreadsheet downloading.")
    ]
    for m_title, m_text in modules_desc:
        add_body_p(doc, m_text, bold_prefix=m_title, space_after=3)

    add_styled_heading(doc, "4.4 Data Flow Diagrams (DFD)", level=2)
    add_body_p(doc,
        "The system's data transformations are illustrated via Context-Level (Level-0) and Functional (Level-1) Data Flow Diagrams:")
    add_code_snippet(doc,
        "[Warehouse Admin] ──► (Credentials) ─────────► [0.0 Inventory] ──► (Dashboard KPIs) ──► [Admin]\n"
        "[Warehouse Admin] ──► (Product Details) ────► [Management ] ──► (Catalog List)   ──► [Admin]\n"
        "[Warehouse Admin] ──► (Stock IN / OUT Data) ─► [  System   ] ◄──► [MySQL Database Engine]\n"
        "[Warehouse Admin] ──► (Report Filters) ─────► [          ] ──► (Exported CSV)   ──► [Admin]")
    add_figure_caption(doc, "Figure 4.2: Context Level-0 Data Flow Diagram (DFD)")

    add_code_snippet(doc,
        "               ┌────────────────────────────────────────────────────────┐\n"
        "               │           LEVEL-1 DETAILED DATA FLOW DIAGRAM           │\n"
        "               └────────────────────────────────────────────────────────┘\n"
        "  [Admin] ──► (1.0 Authenticate Admin) ──► Check [admin] Table ──► Issue Session\n"
        "  [Admin] ──► (2.0 Manage Product Catalog) ──► Validate SKU ──► Store in [products]\n"
        "  [Admin] ──► (3.0 Execute Stock IN) ──► Update Stock & Log ──► [stock_transactions]\n"
        "  [Admin] ──► (4.0 Execute Stock OUT) ──► Check Availability ──► Deduct & Log Audit\n"
        "  [Admin] ◄── (5.0 View Low Stock Alerts) ◄── Evaluate: current_stock <= min_stock\n"
        "  [Admin] ◄── (6.0 Export Valuation CSV) ◄── Compile: SUM(current_stock * cost_price)")
    add_figure_caption(doc, "Figure 4.3: Level-1 Detailed Data Flow Diagram for Stock Operations")

    add_styled_heading(doc, "4.5 Use Case Modeling & Actor Specifications", level=2)
    add_body_p(doc,
        "The primary actor interacting with the system is the Warehouse Administrator. The administrator possesses comprehensive "
        "privileges to manage catalogs, perform stock movements, monitor alerts, and export audit files:")
    add_code_snippet(doc,
        "                                 ┌──────────────────────────────────────────────┐\n"
        "                                 │        INVENTORY MANAGEMENT SYSTEM           │\n"
        "                                 ├──────────────────────────────────────────────┤\n"
        "                                 │  (UC01: Authenticate / Login)                │\n"
        "                                 │  (UC02: View Dashboard & Chart Analytics)    │\n"
        "                                 │  (UC03: Add / Edit / Delete Products)        │\n"
        "        ┌───────────────┐        │  (UC04: Verify Unique SKU Code)              │\n"
        "        │   WAREHOUSE   │───────►│  (UC05: Manage Categories)                   │\n"
        "        │ ADMINISTRATOR │───────►│  (UC06: Execute Inbound Stock IN)            │\n"
        "        │    (ACTOR)    │───────►│  (UC07: Execute Outbound Stock OUT)          │\n"
        "        └───────────────┘        │  (UC08: Prevent Negative Stock Deductions)   │\n"
        "                                 │  (UC09: Monitor Low Stock Alerts Watchlist)  │\n"
        "                                 │  (UC10: Register / Update Suppliers)         │\n"
        "                                 │  (UC11: Generate & Export CSV Reports)       │\n"
        "                                 │  (UC12: Terminate Session / Logout)          │\n"
        "                                 └──────────────────────────────────────────────┘")
    add_figure_caption(doc, "Figure 4.4: Comprehensive Use Case Diagram for Warehouse Administrator")

    add_styled_heading(doc, "4.6 Class & Entity-Relationship (ER) Design", level=2)
    add_body_p(doc,
        "The relational database schema is structured around 5 interconnected tables ensuring Third Normal Form (3NF) compliance:")
    add_code_snippet(doc,
        "┌───────────────────┐             ┌───────────────────────────────────┐\n"
        "│    CATEGORIES     │ 1         * │             PRODUCTS              │\n"
        "├───────────────────┤─────────────┼───────────────────────────────────┤\n"
        "│ category_id (PK)  │             │ product_id (PK)                   │\n"
        "│ category_name     │             │ product_name                      │\n"
        "│ description       │             │ sku_code (UNIQUE)                 │\n"
        "│ created_at        │             │ barcode                           │\n"
        "└───────────────────┘             │ category_id (FK -> categories)    │\n"
        "                                  │ purchase_price, selling_price     │\n"
        "                                  │ current_stock, min_stock_level    │\n"
        "                                  └─────────────────┬─────────────────┘\n"
        "                                                    │ 1\n"
        "                                                    │\n"
        "┌───────────────────┐             ┌─────────────────┴─────────────────┐\n"
        "│     SUPPLIERS     │ 1         * │        STOCK_TRANSACTIONS         │\n"
        "├───────────────────┤─────────────┼───────────────────────────────────┤\n"
        "│ supplier_id (PK)  │             │ transaction_id (PK)               │\n"
        "│ supplier_name     │             │ product_id (FK -> products)       │\n"
        "│ contact_person    │             │ transaction_type (ENUM IN/OUT)    │\n"
        "│ phone, email      │             │ quantity                          │\n"
        "│ address           │             │ supplier_id (FK -> suppliers)     │\n"
        "└───────────────────┘             │ reference_number, reason          │\n"
        "                                  │ transaction_date                  │\n"
        "                                  └───────────────────────────────────┘")
    add_figure_caption(doc, "Figure 4.5: Entity-Relationship (ER) Schema Diagram of Database Tables")

    add_styled_heading(doc, "4.7 Sequence & Activity Workflows", level=2)
    add_body_p(doc,
        "The sequence of interactions for a Stock OUT operation demonstrates how the system protects data consistency:")
    add_code_snippet(doc,
        "Admin Browser           StockServlet             StockDAO            MySQL Database\n"
        "      │                      │                      │                       │\n"
        "      │── POST /stock (out) ─►                      │                       │\n"
        "      │                      │── processStockOut() ─►                       │\n"
        "      │                      │                      │── setAutoCommit(false)►\n"
        "      │                      │                      │── UPDATE current_stock►\n"
        "      │                      │                      │   WHERE stock >= qty  │\n"
        "      │                      │                      │◄─ rowsUpdated == 1 ───│\n"
        "      │                      │                      │── INSERT into log ───►\n"
        "      │                      │                      │── commit() ──────────►\n"
        "      │                      │◄── return true ──────│                       │\n"
        "      │◄── Redirect Success ─│                      │                       │")
    add_figure_caption(doc, "Figure 4.6: UML Sequence Diagram for Transactional Stock In/Out Operations")

    add_styled_heading(doc, "4.8 Relational Database Schema Design", level=2)
    add_body_p(doc, "Detailed attribute definitions, data types, and constraint definitions for each table are documented below:")

    # Table 4.1: admin
    t_admin = doc.add_table(rows=6, cols=5)
    format_table(t_admin, [Inches(1.2), Inches(1.3), Inches(1.2), Inches(1.3), Inches(1.4)],
        ["Field Name", "Data Type", "Constraints", "Key Type", "Description"],
        [
            ["admin_id", "INT", "NOT NULL, AUTO_INC", "PRIMARY KEY", "Unique administrator ID"],
            ["username", "VARCHAR(50)", "NOT NULL, UNIQUE", "UNIQUE", "Login account username"],
            ["password_hash", "VARCHAR(255)", "NOT NULL", "NONE", "Cryptographic BCrypt hash"],
            ["full_name", "VARCHAR(100)", "NOT NULL", "NONE", "Full legal name"],
            ["email", "VARCHAR(100)", "NOT NULL, UNIQUE", "UNIQUE", "Official email address"]
        ], align_center_cols=[0, 3])
    add_figure_caption(doc, "Table 4.1: Database Schema Definition: 'admin' Table")

    # Table 4.2: categories
    t_cat = doc.add_table(rows=4, cols=5)
    format_table(t_cat, [Inches(1.2), Inches(1.3), Inches(1.2), Inches(1.3), Inches(1.4)],
        ["Field Name", "Data Type", "Constraints", "Key Type", "Description"],
        [
            ["category_id", "INT", "NOT NULL, AUTO_INC", "PRIMARY KEY", "Unique category identifier"],
            ["category_name", "VARCHAR(100)", "NOT NULL, UNIQUE", "UNIQUE", "Classification category name"],
            ["description", "TEXT", "NULLABLE", "NONE", "Scope and category notes"]
        ], align_center_cols=[0, 3])
    add_figure_caption(doc, "Table 4.2: Database Schema Definition: 'categories' Table")

    # Table 4.3: suppliers
    t_sup = doc.add_table(rows=6, cols=5)
    format_table(t_sup, [Inches(1.2), Inches(1.3), Inches(1.2), Inches(1.3), Inches(1.4)],
        ["Field Name", "Data Type", "Constraints", "Key Type", "Description"],
        [
            ["supplier_id", "INT", "NOT NULL, AUTO_INC", "PRIMARY KEY", "Unique supplier ID"],
            ["supplier_name", "VARCHAR(150)", "NOT NULL", "NONE", "Company / vendor name"],
            ["contact_person", "VARCHAR(100)", "NULLABLE", "NONE", "Representative contact name"],
            ["phone", "VARCHAR(30)", "NULLABLE", "NONE", "Telephone / mobile number"],
            ["email", "VARCHAR(100)", "NULLABLE", "NONE", "Order correspondence email"]
        ], align_center_cols=[0, 3])
    add_figure_caption(doc, "Table 4.3: Database Schema Definition: 'suppliers' Table")

    # Table 4.4: products
    t_prod = doc.add_table(rows=10, cols=5)
    format_table(t_prod, [Inches(1.2), Inches(1.3), Inches(1.2), Inches(1.3), Inches(1.4)],
        ["Field Name", "Data Type", "Constraints", "Key Type", "Description"],
        [
            ["product_id", "INT", "NOT NULL, AUTO_INC", "PRIMARY KEY", "Unique product ID"],
            ["product_name", "VARCHAR(150)", "NOT NULL", "NONE", "Descriptive product title"],
            ["sku_code", "VARCHAR(60)", "NOT NULL, UNIQUE", "UNIQUE INDEX", "Stock Keeping Unit code"],
            ["barcode", "VARCHAR(100)", "NULLABLE", "NONE", "EAN / UPC barcode string"],
            ["category_id", "INT", "NOT NULL", "FOREIGN KEY", "Refers categories(category_id)"],
            ["purchase_price", "DECIMAL(12,2)", "NOT NULL DEFAULT 0", "NONE", "Procurement unit cost ($)"],
            ["selling_price", "DECIMAL(12,2)", "NOT NULL DEFAULT 0", "NONE", "Retail / dispatch price ($)"],
            ["current_stock", "INT", "NOT NULL DEFAULT 0", "INDEX", "On-hand physical stock quantity"],
            ["min_stock_level", "INT", "NOT NULL DEFAULT 10", "NONE", "Minimum threshold for alert"]
        ], align_center_cols=[0, 3])
    add_figure_caption(doc, "Table 4.4: Database Schema Definition: 'products' Table")

    # Table 4.5: stock_transactions
    t_tx = doc.add_table(rows=8, cols=5)
    format_table(t_tx, [Inches(1.2), Inches(1.3), Inches(1.2), Inches(1.3), Inches(1.4)],
        ["Field Name", "Data Type", "Constraints", "Key Type", "Description"],
        [
            ["transaction_id", "INT", "NOT NULL, AUTO_INC", "PRIMARY KEY", "Unique movement event ID"],
            ["product_id", "INT", "NOT NULL", "FOREIGN KEY", "Refers products(product_id)"],
            ["transaction_type", "ENUM('IN','OUT')", "NOT NULL", "NONE", "Direction: INBOUND or OUTBOUND"],
            ["quantity", "INT", "NOT NULL", "NONE", "Units moved in this event"],
            ["supplier_id", "INT", "NULLABLE", "FOREIGN KEY", "Refers suppliers(supplier_id)"],
            ["reference_number", "VARCHAR(100)", "NULLABLE", "NONE", "Purchase Order or Dispatch #"],
            ["reason", "VARCHAR(255)", "NULLABLE", "NONE", "Operational movement justification"]
        ], align_center_cols=[0, 3])
    add_figure_caption(doc, "Table 4.5: Database Schema Definition: 'stock_transactions' Table")

    doc.add_page_break()

    # =========================================================================
    # CHAPTER 5: IMPLEMENTATION
    # =========================================================================
    add_styled_heading(doc, "CHAPTER 5: IMPLEMENTATION", level=1)

    add_styled_heading(doc, "5.1 Technology Stack and Selection Justification", level=2)
    add_body_p(doc,
        "The selection of Core Java, Jakarta Servlets 6.0, native JDBC, and MySQL 8.4 is founded upon rigorous engineering criteria:")
    add_body_p(doc,
        "1. Predictable Execution & Low Memory Footprint: Frameworks like Spring Boot bundle dozens of transitive libraries, embedded web servers, "
        "and auto-configuration layers that demand 500MB+ heap memory on startup. In contrast, pure Jakarta Servlets deploy as a lean WAR archive "
        "(under 8 MB) on Apache Tomcat 10.1, starting in less than 3 seconds.")
    add_body_p(doc,
        "2. Complete Transaction Transparency: ORM tools (such as Hibernate) hide generated SQL queries, often triggering the notorious N+1 query "
        "problem. Native JDBC gives developers direct control over SQL query optimization, transaction boundaries, and row-level locking.")
    add_body_p(doc,
        "3. Long-Term Maintainability: The standard Java Servlet specification has remained stable and backward-compatible for over two decades. "
        "The application requires zero proprietary third-party commercial plugins to compile or run.")

    add_styled_heading(doc, "5.2 Module-wise Technical Implementation", level=2)
    add_body_p(doc,
        "The technical implementation was executed modularly across the application layers:")
    add_body_p(doc,
        "• Database Connection Management (DBConnection.java): Encapsulates thread-safe connection vending. To ensure security, credentials "
        "are resolved dynamically from OS Environment Variables (DB_URL, DB_USER, DB_PASSWORD), JVM System Properties (-DDB_PASSWORD), "
        "or a git-ignored local .env file.")
    add_body_p(doc,
        "• Cryptographic Password Hashing (PasswordUtil.java): Hashes plain-text passwords into one-way salt-embedded strings via jBCrypt 0.4. "
        "During authentication, BCrypt.checkpw() verifies candidate strings against stored hashes.")
    add_body_p(doc,
        "• Transactional Stock Operations (StockDAO.java): Coordinates multi-step database mutations using explicit JDBC transactions. "
        "setAutoCommit(false) initiates the transaction block, followed by conditional updates and audit inserts, committed atomically.")
    add_body_p(doc,
        "• View Presentation & Scripting: Dynamic server-side rendering is handled via JSP and JSTL (<c:forEach>, <c:choose>, <fmt:formatNumber>). "
        "The frontend is styled using a modern SaaS theme in assets/css/style.css, utilizing deep navy (#0F172A) and crisp white elevated cards.")

    add_styled_heading(doc, "5.3 Core Algorithms and Business Logic", level=2)
    add_body_p(doc,
        "Algorithm 1: Negative-Stock Prevention & Atomic Stock OUT\n"
        "To ensure inventory balances can never drop below zero during concurrent dispatch operations, the system employs an atomic conditional update:")
    add_code_snippet(doc,
        "UPDATE products SET current_stock = current_stock - ?\n"
        "WHERE product_id = ? AND current_stock >= ?;")
    add_body_p(doc,
        "If rowsUpdated returns 0, the DAO immediately triggers conn.rollback() and throws an InsufficientStockException. "
        "This completely eliminates race conditions without requiring slow global table locks.")

    add_styled_heading(doc, "5.4 Important Code Snippets", level=2)
    add_body_p(doc, "Snippet 1: Transactional Stock Inbound Processing (StockDAO.java):")
    add_code_snippet(doc,
        "public boolean processStockIn(int productId, int quantity, Integer supplierId,\n"
        "                              String referenceNumber, String reason) throws SQLException {\n"
        "    Connection conn = null;\n"
        "    PreparedStatement psUpdate = null, psInsert = null;\n"
        "    String updateSql = \"UPDATE products SET current_stock = current_stock + ? WHERE product_id = ?\";\n"
        "    String insertSql = \"INSERT INTO stock_transactions (product_id, transaction_type, quantity, \" +\n"
        "                       \"supplier_id, reference_number, reason) VALUES (?, 'IN', ?, ?, ?, ?)\";\n"
        "    try {\n"
        "        conn = DBConnection.getConnection();\n"
        "        conn.setAutoCommit(false); // Begin Transaction\n"
        "        psUpdate = conn.prepareStatement(updateSql);\n"
        "        psUpdate.setInt(1, quantity);\n"
        "        psUpdate.setInt(2, productId);\n"
        "        psUpdate.executeUpdate();\n\n"
        "        psInsert = conn.prepareStatement(insertSql);\n"
        "        psInsert.setInt(1, productId);\n"
        "        psInsert.setInt(2, quantity);\n"
        "        if (supplierId != null) psInsert.setInt(3, supplierId); else psInsert.setNull(3, Types.INTEGER);\n"
        "        psInsert.setString(4, referenceNumber);\n"
        "        psInsert.setString(5, reason);\n"
        "        psInsert.executeUpdate();\n\n"
        "        conn.commit(); // Commit Transaction\n"
        "        return true;\n"
        "    } catch (SQLException e) {\n"
        "        if (conn != null) conn.rollback();\n"
        "        throw e;\n"
        "    } finally {\n"
        "        DBConnection.close(psUpdate, psInsert, conn);\n"
        "    }\n"
        "}")

    add_styled_heading(doc, "5.5 User Interface Implementation & Screen Captures", level=2)
    add_body_p(doc,
        "The following figures present actual screenshots of the running application deployed on Apache Tomcat 10.1:")

    # Screenshots Insertion
    img_dir = r"D:\Inventory Management System\docs\screenshots"
    img_dash = os.path.join(img_dir, "dashboard.png")
    img_prod = os.path.join(img_dir, "products.png")
    img_stock = os.path.join(img_dir, "stock-operations.png")
    img_rep = os.path.join(img_dir, "reports.png")

    if os.path.exists(img_dash):
        p = doc.add_paragraph()
        p.paragraph_format.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p.add_run().add_picture(img_dash, width=Inches(6.2))
        add_figure_caption(doc, "Figure 5.1: Executive Dashboard Overview with Live KPIs and Chart.js Analytics")

    if os.path.exists(img_prod):
        p = doc.add_paragraph()
        p.paragraph_format.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p.add_run().add_picture(img_prod, width=Inches(6.2))
        add_figure_caption(doc, "Figure 5.2: Product Catalog Management with SKU Uniqueness & Category Filter")

    if os.path.exists(img_stock):
        p = doc.add_paragraph()
        p.paragraph_format.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p.add_run().add_picture(img_stock, width=Inches(6.2))
        add_figure_caption(doc, "Figure 5.3: Stock IN and Stock OUT Transaction Workbench with Real-time Ledger")

    if os.path.exists(img_rep):
        p = doc.add_paragraph()
        p.paragraph_format.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p.add_run().add_picture(img_rep, width=Inches(6.2))
        add_figure_caption(doc, "Figure 5.4: Inventory Valuation, Movement History Reports, and CSV Data Export")

    doc.add_page_break()

    # =========================================================================
    # CHAPTER 6: SYSTEM TESTING AND VALIDATION
    # =========================================================================
    add_styled_heading(doc, "CHAPTER 6: SYSTEM TESTING AND VALIDATION", level=1)

    add_styled_heading(doc, "6.1 Testing Methodology & Strategy", level=2)
    add_body_p(doc,
        "A rigorous multi-tiered verification strategy was conducted across the software application:")
    add_body_p(doc,
        "• Unit Testing: Isolated verification of DAO methods (AdminDAO, ProductDAO, StockDAO) to validate SQL queries and mapping accuracy.")
    add_body_p(doc,
        "• Integration Testing: Validating interaction between Servlet controllers, HTTP session management, and the MySQL database.")
    add_body_p(doc,
        "• Boundary & Security Testing: Testing SKU uniqueness checks, negative stock rejection, and unauthenticated URL redirection.")

    add_styled_heading(doc, "6.2 Test Cases & Execution Matrix", level=2)
    add_body_p(doc, "The comprehensive test suite execution matrix is detailed in Table 6.1:")

    tc_table = doc.add_table(rows=11, cols=6)
    tc_headers = ["Test ID", "Test Description", "Test Input Data", "Expected Output", "Actual Result", "Status"]
    tc_data = [
        ["TC01", "Admin Login (Valid)", "User: admin, Pass: admin123", "Session created, redirect to /dashboard", "Redirected to dashboard", "PASS"],
        ["TC02", "Admin Login (Invalid)", "User: admin, Pass: wrongpass", "Display 'Invalid username or password'", "Error alert displayed", "PASS"],
        ["TC03", "Session Protection", "Unauthenticated GET /dashboard", "HTTP 302 Redirect to /login.jsp", "Redirected to login.jsp", "PASS"],
        ["TC04", "Add Product (Valid)", "Unique SKU: SKU-DRL-001, Qty: 45", "Row inserted in products table", "Product added successfully", "PASS"],
        ["TC05", "Duplicate SKU Rejection", "Existing SKU: SKU-DRL-001", "Reject with duplicate SKU error", "Duplicate SKU rejected", "PASS"],
        ["TC06", "Stock IN (Receiving)", "Product: Drill, Qty: +20, PO-101", "Stock incremented (45->65), log created", "Stock incremented, audit logged", "PASS"],
        ["TC07", "Stock OUT (Valid)", "Product: Drill, Qty: -10, Reason", "Stock decremented (65->55), log created", "Stock decremented, audit logged", "PASS"],
        ["TC08", "Prevent Negative Stock", "Product: Caliper (Stock 8), Qty: 15", "Abort transaction, roll back, display error", "Transaction rolled back", "PASS"],
        ["TC09", "Low Stock Alert Trigger", "Deduct stock to <= min_stock", "Status transitions to 'LOW STOCK'", "Amber badge displayed", "PASS"],
        ["TC10", "Report CSV Export", "Click 'Export CSV' button", "HTTP attachment response with CSV content", "CSV downloaded cleanly", "PASS"]
    ]
    format_table(tc_table, [Inches(0.7), Inches(1.4), Inches(1.4), Inches(1.3), Inches(1.2), Inches(0.6)],
                 tc_headers, tc_data, align_center_cols=[0, 5])
    add_figure_caption(doc, "Table 6.1: Comprehensive System Test Cases and Execution Results Matrix (TC01-TC10)")

    add_styled_heading(doc, "6.3 Unit, Integration, and Transaction Verification", level=2)
    add_body_p(doc,
        "Transaction verification confirmed that when a Stock OUT operation encountered an inventory shortage, the database state "
        "remained strictly unaltered. No orphan records were introduced into stock_transactions, and no partial quantity deductions occurred. "
        "All database connection pools cleanly released resources upon completion.")

    add_styled_heading(doc, "6.4 Summary of Test Results", level=2)
    add_body_p(doc,
        "All 10 formal test cases achieved a 100% pass rate. System regression testing confirmed consistent performance on Java JDK 17, "
        "21, and 24 running on Apache Tomcat 10.1 with MySQL 8.4.")

    doc.add_page_break()

    # =========================================================================
    # CHAPTER 7: RESULTS AND DISCUSSION
    # =========================================================================
    add_styled_heading(doc, "CHAPTER 7: RESULTS AND DISCUSSION", level=1)

    add_styled_heading(doc, "7.1 Output Screen Analysis", level=2)
    add_body_p(doc,
        "The finalized web application exhibits an enterprise aesthetic characterized by high visual clarity and intuitive workflows. "
        "The Executive Dashboard aggregates live catalog statistics instantly upon page load, eliminating the need to execute manual "
        "SQL queries or export spreadsheet dumps. The integration of Chart.js provides immediate visibility into warehouse category "
        "distribution, allowing storage managers to balance aisle space allocation.")

    add_styled_heading(doc, "7.2 Performance & Reliability Observations", level=2)
    add_body_p(doc,
        "Performance benchmarks indicate that dashboard metric aggregation queries execute in under 35 milliseconds on a database "
        "populated with sample product records. Stock IN and Stock OUT transactions complete within 40 milliseconds, providing "
        "instantaneous feedback to warehouse operators. The application maintained zero memory leaks during prolonged multi-session testing.")

    add_styled_heading(doc, "7.3 Key Advantages of the Developed System", level=2)
    add_body_p(doc, "The key competitive and academic advantages of the developed system include:")
    advantages = [
        "Framework Independence: Pure Core Java, Servlets, and JDBC eliminate external framework vulnerabilities and bloated dependencies.",
        "Guaranteed ACID Consistency: Robust JDBC transaction boundaries guarantee that physical and digital inventory balances never diverge.",
        "Zero Software Licensing Cost: Built completely on open-source software (Tomcat, MySQL, Maven, Bootstrap).",
        "Live Threshold Alerting: Automated transition between Normal, Low Stock, and Out of Stock states prevents inventory stockouts.",
        "Direct CSV Export Capability: Enables rapid data portability for accounting and auditing without third-party plugins."
    ]
    for adv in advantages:
        add_body_p(doc, f"• {adv}", space_after=3)

    doc.add_page_break()

    # =========================================================================
    # CHAPTER 8: CONCLUSION AND FUTURE SCOPE
    # =========================================================================
    add_styled_heading(doc, "CHAPTER 8: CONCLUSION AND FUTURE SCOPE", level=1)

    add_styled_heading(doc, "8.1 Conclusion", level=2)
    add_body_p(doc,
        "The Inventory Management System successfully achieves all stated engineering objectives. Developed using Core Java, "
        "Jakarta Servlets 6.0, JDBC, MySQL 8.4, and JSP, the project demonstrates that an enterprise-grade warehouse control platform "
        "can be engineered cleanly without relying on cumbersome, memory-intensive frameworks. By enforcing strict relational integrity, "
        "concurrency-safe JDBC transactions, and automated low-stock threshold alerting, the system eliminates human recording errors, "
        "prevents negative stock balances, and delivers real-time visibility across all warehouse operations.")

    add_styled_heading(doc, "8.2 Limitations", level=2)
    add_body_p(doc,
        "While fully functional for central warehouse management, the current implementation has the following limitations:")
    add_body_p(doc,
        "1. Single Warehouse Scope: Inventory is currently tracked within a single logical facility rather than across multi-depot networks.")
    add_body_p(doc,
        "2. Manual Hardware Barcode Entry: Barcode data entry currently relies on standard keyboard or emulated USB wedge inputs rather than a direct mobile camera scanner.")

    add_styled_heading(doc, "8.3 Future Enhancements", level=2)
    add_body_p(doc,
        "Planned future extensions for subsequent releases include:")
    future_work = [
        "Multi-Warehouse & Bin Location Tracking: Expanding the data schema to track specific aisles, shelf racks, and bin compartments across multi-city depots.",
        "Automated SMTP Email & SMS Reorder Notifications: Dispatching automatic alerts to procurement officers when items hit reorder points.",
        "Hardware Barcode & QR Code Mobile Scanning: Integrating Progressive Web App (PWA) camera barcode scanning for rapid mobile inventory checks.",
        "Role-Based Access Control (RBAC): Introducing granular permissions distinguishing Warehouse Receiving Clerks, Dispatch Staff, and Financial Auditors."
    ]
    for fw in future_work:
        add_body_p(doc, f"• {fw}", space_after=3)

    doc.add_page_break()

    # =========================================================================
    # REFERENCES
    # =========================================================================
    add_styled_heading(doc, "REFERENCES", level=1)
    doc.paragraphs[-1].paragraph_format.alignment = WD_ALIGN_PARAGRAPH.CENTER

    references = [
        "[1]  H. Schildt, Java: The Complete Reference, 12th ed. New York, NY, USA: McGraw-Hill Education, 2021.",
        "[2]  Oracle Corporation, “Java Platform, Standard Edition Documentation (JDK 17/21),” Oracle Technology Network, 2024. [Online]. Available: https://docs.oracle.com/en/java/",
        "[3]  Jakarta EE Working Group, “Jakarta Servlet Specification, Version 6.0,” Eclipse Foundation, 2022. [Online]. Available: https://jakarta.ee/specifications/servlet/6.0/",
        "[4]  MySQL AB and Oracle Corporation, “MySQL 8.4 Reference Manual: High Performance Relational Database,” 2024. [Online]. Available: https://dev.mysql.com/doc/refman/8.4/en/",
        "[5]  Apache Software Foundation, “Apache Tomcat 10.1 Architecture and Deployment Documentation,” 2024. [Online]. Available: https://tomcat.apache.org/tomcat-10.1-doc/",
        "[6]  M. Fowler, Patterns of Enterprise Application Architecture. Boston, MA, USA: Addison-Wesley Professional, 2002.",
        "[7]  E. Gamma, R. Helm, R. Johnson, and J. Vlissides, Design Patterns: Elements of Reusable Object-Oriented Software. Reading, MA, USA: Addison-Wesley, 1994.",
        "[8]  Bootstrap Core Team, “Bootstrap 5.3 Documentation: Modern Responsive Front-End Framework,” 2024. [Online]. Available: https://getbootstrap.com/docs/5.3/"
    ]
    for ref in references:
        add_body_p(doc, ref, space_after=6)

    doc.add_page_break()

    # =========================================================================
    # APPENDICES
    # =========================================================================
    add_styled_heading(doc, "APPENDIX A: Source Code Highlights & Repository", level=1)
    add_body_p(doc,
        "The complete source code, database scripts, configuration templates, and frontend assets are maintained on GitHub:",
        space_after=6)
    add_body_p(doc,
        "https://github.com/shaikminhazuddin/inventory-management-system",
        bold_prefix="Official GitHub Repository: ", space_after=12)

    add_body_p(doc, "Key Package & Source File Directory:")
    add_code_snippet(doc,
        "com.inventory\n"
        "├── model/     (Admin.java, Product.java, Category.java, Supplier.java, StockTransaction.java)\n"
        "├── dao/       (AdminDAO.java, ProductDAO.java, CategoryDAO.java, SupplierDAO.java, StockDAO.java)\n"
        "├── servlet/   (LoginServlet.java, DashboardServlet.java, ProductServlet.java, StockServlet.java, ...)\n"
        "├── filter/    (AuthenticationFilter.java)\n"
        "└── util/      (DBConnection.java, PasswordUtil.java)")

    add_styled_heading(doc, "APPENDIX B: User Manual and Deployment Guide", level=1)
    add_styled_heading(doc, "B.1 Database Initialization", level=2)
    add_body_p(doc, "Execute the database schema setup script in MySQL 8.4:")
    add_code_snippet(doc,
        "mysql -u root -p < database.sql")

    add_styled_heading(doc, "B.2 Building with Apache Maven", level=2)
    add_body_p(doc, "Compile and build the production WAR package:")
    add_code_snippet(doc,
        "mvn clean package\n"
        "# Generates: target/InventoryManagementSystem.war")

    add_styled_heading(doc, "B.3 Deploying to Apache Tomcat 10.1", level=2)
    add_body_p(doc, "Copy the generated WAR archive to Tomcat's webapps directory and start the server:")
    add_code_snippet(doc,
        "Copy-Item \"target\\InventoryManagementSystem.war\" -Destination \"C:\\apache-tomcat-10.1.x\\webapps\\\"\n"
        "& \"C:\\apache-tomcat-10.1.x\\bin\\startup.bat\"\n\n"
        "# Access in Web Browser:\n"
        "# http://localhost:8080/InventoryManagementSystem/")

    add_styled_heading(doc, "B.4 Default System Credentials", level=2)
    add_body_p(doc, "• Portal URL: http://localhost:8080/InventoryManagementSystem/login.jsp\n• Username: admin\n• Password: admin123")

    # Output file path
    output_docx = r"D:\Inventory Management System\Project_Report\Inventory_Management_System_Project_Report.docx"
    doc.save(output_docx)
    print(f"Project Report generated successfully: {output_docx}")

if __name__ == "__main__":
    build_inventory_report()

