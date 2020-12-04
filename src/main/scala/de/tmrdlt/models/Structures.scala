package de.tmrdlt.models


/**
 * Meta information container for APIListResult.
 *
 * @param totalEntries Number of total entries.
 * @param offset       Offset of this page.
 * @param limit        Number of entries displayed on this page.
 * @param next         Relative URI to next page
 * @param previous     Relative URI to previous page
 */
case class ApiListResultMeta(totalEntries: Long,
                             offset: Long,
                             limit: Long,
                             next: Option[String],
                             previous: Option[String])

/**
 * An API response containing additional data, a list of datas and meta information.
 *
 * @param additionalData A result of type C.
 * @param data           A list of results of type T.
 * @param meta           Meta information for pagination.
 * @tparam C A json-able type.
 * @tparam T A json-able type.
 */
case class ApiListResult[C, T](additionalData: Option[C],
                               data: Seq[T],
                               meta: ApiListResultMeta)