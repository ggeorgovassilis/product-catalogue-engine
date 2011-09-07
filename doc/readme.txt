Documentation for PCE

PCE is a library that can read products and components from an Excel file. It can also execute rules written in javascript on the components.
Wording:
Entity: An entity is a line written in the Excel file and models things as a line of flat attributes. 
ID: Every entity has a mandatory ID column. Every entity ID must be unique in the Excel file.
Attribute: A column models an attribute of an entity. We try to not assign any meaning to attributes for the most part.

What we can model:
Simple attributes on products (size, names, manufacturers, colours, prices and more things)
Products that reference to other entities

Rules:
We can model rules that
- compute values for attributes
- compute lists of IDs for attributes