package com.example.shabyttan.models

data class Data(
    val accession_number: String,
    val alternate_images: List<AlternateImage>,
    val artists_tags: List<String>,
    val athena_id: Int,
    val citations: List<Citation>,
    val collection: String,
    val copyright: Any,
    val creation_date: String,
    val creation_date_earliest: Int,
    val creation_date_latest: Int,
    val creators: List<Creator>,
    val creditline: String,
    val culture: List<String>,
    val current_location: String,
    val department: String,
    val digital_description: Any,
    val dimensions: Dimensions,
    val edition_of_the_work: Any,
    val exhibitions: Exhibitions,
    val external_resources: ExternalResources,
    val find_spot: Any,
    val former_accession_numbers: List<String>,
    val fun_fact: String,
    val id: Int,
    val images: Images,
    val inscriptions: List<Inscription>,
    val measurements: String,
    val provenance: List<Provenance>,
    val related_works: List<RelatedWork>,
    val share_license_status: String,
    val sketchfab_id: Any,
    val sketchfab_url: Any,
    val state_of_the_work: Any,
    val support_materials: List<Any>,
    val technique: String,
    val title: String,
    val tombstone: String,
    val type: String,
    val updated_at: String,
    val url: String,
    val wall_description: String
)