using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.HttpsPolicy;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Options;
using Microsoft.EntityFrameworkCore;
using SilentManagerApiServer.Models.DB;
using SilentManagerApiServer.DataAccess;
using System.Diagnostics;
using SilentManagerApiServer.BusinessLogic.Dtos;
using SilentManagerApiServer.BusinessLogic.Service;
using Microsoft.AspNetCore.Identity;
using SilentManager.API.Models.DB;
using SilentManager.API.Security;
using SilentManager.API;
using SilentManager.API.BusinessLogic.Service;
using SilentManager.API.Models;
using SilentManager.API.BusinessLogic.Dtos;

namespace SilentManagerApiServer
{
    public class Startup
    {
        public Startup(IConfiguration configuration)
        {
            Configuration = configuration;
        }

        public IConfiguration Configuration { get; }

        // This method gets called by the runtime. Use this method to add services to the container.
        public void ConfigureServices(IServiceCollection services)
        {
            services.AddMvc().SetCompatibilityVersion(CompatibilityVersion.Version_2_1);
            services.AddApiVersioning(o => {
                o.ReportApiVersions = true;
                o.AssumeDefaultVersionWhenUnspecified = true;
                o.DefaultApiVersion = new ApiVersion(1, 0);
            });
            int a = Configuration.GetValue<int>("PageSize");
            // Set DB context
            services.AddDbContext<SilentManagerContext>(options => options.UseSqlServer(Configuration.GetConnectionString("SilentManagerDatabase")));
            services.AddTransient<IUnitOfWork, UnitOfWork>();
            services.AddTransient<IUserService, UserService>();
            services.AddTransient<IEventService, EventService>();
            services.AddTransient<ILoginService, LoginService>();
            services.AddTransient<IInviteService, InviteService>();



            services.AddCors();
            services.AddMvc().SetCompatibilityVersion(CompatibilityVersion.Version_2_1);
        }

        // This method gets called by the runtime. Use this method to configure the HTTP request pipeline.
        public void Configure(IApplicationBuilder app, IHostingEnvironment env, ILoggerFactory loggerFactory)
        {
            if (env.IsDevelopment())
            {
                app.UseDeveloperExceptionPage();
                //app.UseIdentityServer();
            }
            else
            {
                app.UseHsts();
            }

            AutoMapper.Mapper.Initialize(cfg =>
            {
                cfg.CreateMap<Models.DB.User, UserOutDto>();
                cfg.CreateMap<Models.DB.User, UserInDto>();
                cfg.CreateMap<UserInDto, Models.DB.User>();
                cfg.CreateMap<UserOutDto, UserInDto>();
                cfg.CreateMap<Event, CompletedEvent>();
                cfg.CreateMap<CompletedEvent, Event>();
                cfg.CreateMap<PersonalSettingsDto, PersonalSettings>();
                cfg.CreateMap<PersonalSettings, PersonalSettingsDto>();
                cfg.CreateMap<PersonalSettings, PersonalSettingsDto>();
                cfg.CreateMap<InviteDto, Invite>();
                cfg.CreateMap<Invite, InviteDto>();
                cfg.CreateMap<InviteState, InviteStateDto>();
                cfg.CreateMap<InviteStateDto, InviteState>();
                cfg.CreateMap<CompletedLocation, Location>();
            });

            //app.UseHttpsRedirection();
            app.UseMvc();
        }
    }
}
