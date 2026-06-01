using System.Text.RegularExpressions;

namespace Slugify.MultiLang;

public class SlugifySlugOptions
{
    public string Replacement { get; set; } = "-";
    public Regex? Remove { get; set; }
    public bool Lower { get; set; } = true;
    public bool Strict { get; set; } = true;
    public bool Trim { get; set; } = true;
    public string? Locale { get; set; }
}
